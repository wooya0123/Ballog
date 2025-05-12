package com.ballog.mobile.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ballog.mobile.BallogApplication
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.dto.*
import com.ballog.mobile.data.model.*
import com.ballog.mobile.data.model.toVideo
import com.ballog.mobile.util.S3Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson

import java.io.File

class VideoViewModel : ViewModel() {
    private val tokenManager = BallogApplication.getInstance().tokenManager
    private val videoApi = RetrofitInstance.videoApi

    private val _video = MutableStateFlow<Video?>(null)
    val video: StateFlow<Video?> = _video.asStateFlow()

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
    fun getMatchVideo(matchId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = tokenManager.getAccessToken().first() ?: return@launch
                val response = videoApi.getMatchVideo("Bearer $token", matchId)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true && apiResponse.result != null) {
                        _video.value = apiResponse.result.toVideo()
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
                val token = tokenManager.getAccessToken().first() ?: return@launch
                videoApi.deleteQuarterVideo("Bearer $token", videoId)
                // 삭제 후 재조회 등 처리 가능
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
                val token = tokenManager.getAccessToken().first() ?: return@launch
                videoApi.addHighlight("Bearer $token", request)
                getMatchVideo(request.videoId)  // 하이라이트 추가 후 다시 조회
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * 하이라이트 수정
     */
    fun updateHighlight(request: HighlightUpdateRequest) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getAccessToken().first() ?: return@launch
                videoApi.updateHighlight("Bearer $token", request)
                getMatchVideo(_video.value?.id ?: return@launch)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * 하이라이트 삭제
     */
    fun deleteHighlight(highlightId: Int) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getAccessToken().first() ?: return@launch
                videoApi.deleteHighlight("Bearer $token", highlightId)
                getMatchVideo(_video.value?.id ?: return@launch)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * 업로드용 Presigned URL을 이미 발급받았고, 실제 업로드는 외부에서 처리된다면 생략 가능
     * 또는 필요시 multipart 업로드 로직도 이곳에 추가 가능
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

                val token = tokenManager.getAccessToken().first()
                if (token == null) {
                    Log.e("VideoViewModel", "⛔ 토큰 없음 - 업로드 중단")
                    return@launch
                }

                val request = PresignedVideoUploadRequest(
                    matchId = matchId,
                    quarterNumber = quarterNumber,
                    duration = duration,
                    fileName = file.name
                )

                // ✅ 여기에 JSON 바디 로그 추가
                val json = Gson().toJson(request)
                Log.d("VideoViewModel", "📦 요청 JSON 바디: $json")

                Log.d("VideoViewModel", "🔥 request = $request")
                Log.d("VideoViewModel", "📤 Presigned URL 요청 시작")

                val response = videoApi.getPresignedVideoUploadUrl("Bearer $token", request)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val presignedUrl = response.body()?.result?.url
                    if (!presignedUrl.isNullOrEmpty()) {
                        Log.d("VideoViewModel", "✅ Presigned URL 응답 성공: $presignedUrl")
                        Log.d("VideoViewModel", "📦 S3 업로드 시작: $presignedUrl")

                        val uploadSuccess = withContext(Dispatchers.IO) {
                            S3Utils.putFileToPresignedUrl(presignedUrl, file)
                        }

                        if (uploadSuccess) {
                            Log.d("VideoViewModel", "✅ S3 업로드 성공, 매치 정보 재조회")
                            getMatchVideo(matchId)
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



}
