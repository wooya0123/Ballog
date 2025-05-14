package com.ballog.mobile.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.dto.*
import com.ballog.mobile.ui.video.HighlightUiState
import com.ballog.mobile.ui.video.QuarterVideoData
import com.ballog.mobile.ui.video.VideoUiState
import com.ballog.mobile.util.S3Utils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class VideoViewModel : ViewModel() {

    private val _videoUiState = MutableStateFlow(VideoUiState())
    val videoUiState: StateFlow<VideoUiState> = _videoUiState.asStateFlow()

    private val videoApi = RetrofitInstance.videoApi

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setError(message: String?) {
        _error.value = message
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    /**
     * 쿼터 영상 및 하이라이트 조회
     */
    fun getMatchVideos(matchId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = videoApi.getMatchVideos(matchId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val result = response.body()!!.result
                    Log.d("VideoViewModel", "✅ 쿼터 영상 조회 성공 - 총 ${result.totalQuarters}쿼터")
                    
                    // quarterList가 null인 경우 빈 리스트로 처리
                    val quarterList = result.quarterList ?: emptyList()
                    Log.d("VideoViewModel", "📋 quarterList: ${if (quarterList.isEmpty()) "비어 있음" else quarterList}")
                    
                    _videoUiState.value = VideoUiState(
                        totalQuarters = result.totalQuarters,
                        quarterList = quarterList.map { it.toQuarterVideoData() }
                    )
                } else {
                    val msg = response.body()?.message ?: "쿼터별 영상 조회 실패"
                    Log.e("VideoViewModel", "❌ API 실패 - $msg")
                    _error.value = msg
                    Log.e("VideoViewModel", "⚠️ raw error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("VideoViewModel", "🔥 예외 발생 (getMatchVideos)", e)
                _error.value = "API 호출 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Presigned URL을 통한 쿼터 영상 업로드
     */
    fun uploadQuarterVideo(
        context: Context,
        file: File,
        matchId: Int,
        quarterNumber: Int,
        duration: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val request = PresignedVideoUploadRequest(
                    fileName = file.name
                )

                val json = Gson().toJson(request)
                Log.d("VideoViewModel", "📤 Presigned URL 요청 바디: $json")

                // 1. Presigned URL 발급 요청
                val response = videoApi.requestUploadUrl(request)
                
                Log.d("VideoViewModel", "📥 Presigned URL 응답: isSuccess=${response.body()?.isSuccess}, code=${response.body()?.code}")
                Log.d("VideoViewModel", "📥 응답 메시지: ${response.body()?.message}")
                Log.d("VideoViewModel", "📥 S3 URL: ${response.body()?.result?.s3Url}")

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val presignedUrl = response.body()?.result?.s3Url
                    if (!presignedUrl.isNullOrEmpty()) {
                        Log.d("VideoViewModel", "✅ Presigned URL 수신 성공: $presignedUrl")
                        Log.d("VideoViewModel", "📦 S3 업로드 시작")

                        // 2. 파일을 S3에 업로드
                        val uploadSuccess = withContext(Dispatchers.IO) {
                            S3Utils.putFileToPresignedUrl(presignedUrl, file)
                        }

                        if (uploadSuccess) {
                            Log.d("VideoViewModel", "✅ S3 업로드 성공")
                            
                            // presigned URL에서 쿼리 파라미터 제거
                            val baseS3Url = presignedUrl.split("?")[0]
                            Log.d("VideoViewModel", "🔗 저장할 영상 URL: $baseS3Url")
                            
                            // 3. 영상 저장 요청
                            val saveRequest = SaveVideoRequest(
                                matchId = matchId,
                                quarterNumber = quarterNumber,
                                duration = duration,
                                videoUrl = baseS3Url
                            )
                            
                            val saveResponse = videoApi.saveVideo(saveRequest)
                            if (saveResponse.isSuccessful && saveResponse.body()?.isSuccess == true) {
                                Log.d("VideoViewModel", "✅ 영상 저장 성공")
                            } else {
                                val errorMessage = saveResponse.body()?.message ?: "영상 저장 실패"
                                Log.e("VideoViewModel", "❌ 영상 저장 실패 - $errorMessage")
                                _error.value = errorMessage
                            }
                            
                            // 4. 매치 비디오 목록 갱신
                            getMatchVideos(matchId)
                        } else {
                            Log.e("VideoViewModel", "⛔ S3 업로드 실패")
                            _error.value = "S3 업로드에 실패했습니다"
                        }
                    } else {
                        Log.e("VideoViewModel", "❌ Presigned URL이 비어 있음")
                        _error.value = "Presigned URL이 유효하지 않습니다"
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Presigned URL 요청 실패"
                    Log.e("VideoViewModel", "❌ Presigned URL 요청 실패 - $errorMessage")
                    _error.value = errorMessage
                }
            } catch (e: Exception) {
                Log.e("VideoViewModel", "🔥 업로드 예외 발생", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteVideo(videoId: Int, matchId: Int) {
        viewModelScope.launch {
            try {
                Log.d("VideoViewModel", "🗑️ 영상 삭제 요청: $videoId")
                videoApi.deleteVideo(videoId)  // Path 파라미터로 변경
                getMatchVideos(matchId)
            } catch (e: Exception) {
                Log.e("VideoViewModel", "🔥 영상 삭제 실패", e)
                _error.value = e.message
            }
        }
    }

    fun addHighlight(request: HighlightAddRequest) {
        viewModelScope.launch {
            try {
                Log.d("VideoViewModel", "➕ 하이라이트 추가 요청: ${request.highlightName}")
                videoApi.addHighlight(request)
                getMatchVideos(request.videoId)
            } catch (e: Exception) {
                Log.e("VideoViewModel", "🔥 하이라이트 추가 실패", e)
                _error.value = e.message
            }
        }
    }

    fun updateHighlight(request: HighlightUpdateRequest, matchId: Int) {
        viewModelScope.launch {
            try {
                Log.d("VideoViewModel", "✏️ 하이라이트 수정 요청: ${request.highlightId}")
                videoApi.updateHighlight(request)
                getMatchVideos(matchId)
            } catch (e: Exception) {
                Log.e("VideoViewModel", "🔥 하이라이트 수정 실패", e)
                _error.value = e.message
            }
        }
    }

    fun deleteHighlight(highlightId: Int, matchId: Int) {
        viewModelScope.launch {
            try {
                Log.d("VideoViewModel", "❌ 하이라이트 삭제 요청: $highlightId")
                videoApi.deleteHighlight(highlightId)  // Path 파라미터로 변경
                getMatchVideos(matchId)
            } catch (e: Exception) {
                Log.e("VideoViewModel", "🔥 하이라이트 삭제 실패", e)
                _error.value = e.message
            }
        }
    }

    private fun VideoResponseDto.toQuarterVideoData(): QuarterVideoData {
        return QuarterVideoData(
            videoId = this.videoId ?: -1,
            quarterNumber = this.quarterNumber ?: 1,
            videoUrl = this.videoUrl?: "",
            highlights = this.highlightList.map { dto ->
                val startParts = dto.startTime.split(":")
                val startTime = if (startParts.size >= 3) {
                    val minutes = startParts[1].padStart(2, '0')
                    val seconds = startParts[2].padStart(2, '0')
                    "$minutes:$seconds"
                } else if (startParts.size == 2) {
                    val minutes = startParts[0].padStart(2, '0')
                    val seconds = startParts[1].padStart(2, '0')
                    "$minutes:$seconds"
                } else {
                    "00:00"
                }
                
                val endParts = dto.endTime.split(":")
                val endTime = if (endParts.size >= 3) {
                    val minutes = endParts[1].padStart(2, '0')
                    val seconds = endParts[2].padStart(2, '0')
                    "$minutes:$seconds"
                } else if (endParts.size == 2) {
                    val minutes = endParts[0].padStart(2, '0')
                    val seconds = endParts[1].padStart(2, '0')
                    "$minutes:$seconds"
                } else {
                    "00:00"
                }
                
                HighlightUiState(
                    id = dto.highlightId.toString(),
                    title = dto.highlightName,
                    startMin = startTime,
                    startSec = "",
                    endMin = endTime,
                    endSec = ""
                )
            },
            showPlayer = false
        )
    }
}
