package com.ballog.mobile.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ballog.mobile.BallogApplication
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.dto.*
import com.ballog.mobile.data.model.Video
import com.ballog.mobile.data.model.toVideo
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

    private val tokenManager = BallogApplication.getInstance().tokenManager
    private val videoApi = RetrofitInstance.videoApi

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

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
     * 영상 조회
     */
    fun getMatchVideos(matchId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = videoApi.getMatchVideos(matchId)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true && apiResponse.result != null) {
                        _videos.value = apiResponse.result.quarterList.map { it.toVideo() }
                    } else {
                        _error.value = apiResponse?.message ?: "영상 조회 실패"
                    }
                } else {
                    _error.value = "서버 오류: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 쿼터 영상 삭제
     */
    fun deleteVideo(videoId: Int) {
        viewModelScope.launch {
            try {
                videoApi.deleteVideo(DeleteVideoRequest(videoId))
                // 필요시 getMatchVideos() 호출로 최신 상태 반영
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * 하이라이트 추가
     */
    fun addHighlight(request: HighlightAddRequest) {
        viewModelScope.launch {
            try {
                videoApi.addHighlight(request)
                getMatchVideos(request.videoId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * 하이라이트 수정
     */
    fun updateHighlight(request: HighlightUpdateRequest, matchId: Int) {
        viewModelScope.launch {
            try {
                videoApi.updateHighlight(request)
                getMatchVideos(matchId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * 하이라이트 삭제
     */
    fun deleteHighlight(highlightId: Int, matchId: Int) {
        viewModelScope.launch {
            try {
                videoApi.deleteHighlight(DeleteHighlightRequest(highlightId))
                getMatchVideos(matchId)
            } catch (e: Exception) {
                _error.value = e.message
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
                Log.d("VideoViewModel", "🔄 업로드 시작 - matchId: $matchId, quarter: $quarterNumber, duration: $duration, fileName: ${file.name}")

                val request = PresignedVideoUploadRequest(
                    matchId = matchId,
                    quarterNumber = quarterNumber,
                    duration = duration,
                    fileName = file.name
                )

                val json = Gson().toJson(request)
                Log.d("VideoViewModel", "📦 요청 JSON 바디: $json")
                Log.d("VideoViewModel", "📤 Presigned URL 요청 시작")

                val response = videoApi.requestUploadUrl(request)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val presignedUrl = response.body()?.result?.videoUrl
                    if (!presignedUrl.isNullOrEmpty()) {
                        Log.d("VideoViewModel", "✅ Presigned URL 응답 성공: $presignedUrl")
                        Log.d("VideoViewModel", "📦 S3 업로드 시작: $presignedUrl")

                        val uploadSuccess = withContext(Dispatchers.IO) {
                            S3Utils.putFileToPresignedUrl(presignedUrl, file)
                        }

                        if (uploadSuccess) {
                            Log.d("VideoViewModel", "✅ S3 업로드 성공, 매치 정보 재조회")
                            videoApi.notifyUploadSuccess(
                                UploadSuccessRequest(matchId, quarterNumber)
                            )
                            getMatchVideos(matchId)
                        } else {
                            Log.e("VideoViewModel", "⛔ S3 업로드 실패")
                            _error.value = "S3 업로드에 실패했습니다"
                        }
                    } else {
                        Log.e("VideoViewModel", "❌ Presigned URL이 null이거나 비어 있음")
                        _error.value = "Presigned URL이 유효하지 않습니다"
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Presigned URL 요청 실패"
                    Log.e("VideoViewModel", "❌ Presigned URL 요청 실패 - $errorMessage")
                    _error.value = errorMessage
                }
            } catch (e: Exception) {
                Log.e("VideoViewModel", "🔥 예외 발생: ${e.message}", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
                Log.d("VideoViewModel", "🏁 업로드 종료")
            }
        }
    }

    fun fetchMatchVideoData(matchId: Int) {
        viewModelScope.launch {
            try {
                val response = videoApi.getMatchVideos(matchId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val result = response.body()!!.result
                    _videoUiState.value = VideoUiState(
                        totalQuarters = result.totalQuarters,
                        quarterList = result.quarterList.map { it.toQuarterVideoData() }
                    )
                } else {
                    _error.value = response.body()?.message ?: "쿼터별 영상 조회 실패"
                }
            } catch (e: Exception) {
                Log.e("VideoViewModel", "영상 API 실패", e)
                _error.value = "API 호출 중 오류가 발생했습니다."
            }
        }
    }

    private fun VideoResponseDto.toQuarterVideoData(): QuarterVideoData {
        return QuarterVideoData(
            videoUri = videoUrl?.let { Uri.parse(it) },
            highlights = highlightList.map { dto ->
                val (startHour, startMin) = dto.startTime.split(":").let { it[0] to it[1] }
                val (endHour, endMin) = dto.endTime.split(":").let { it[0] to it[1] }

                HighlightUiState(
                    title = dto.highlightName,
                    startHour = startHour,
                    startMin = startMin,
                    endHour = endHour,
                    endMin = endMin
                )
            },
            showPlayer = false
        )
    }
}
