package com.ballog.mobile.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ballog.mobile.BallogApplication
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.dto.UserInfoResponse
import com.ballog.mobile.data.dto.UserUpdateRequest
import com.ballog.mobile.util.ImageUtils
import com.ballog.mobile.util.S3Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val tokenManager = BallogApplication.getInstance().tokenManager
    private val userApi = RetrofitInstance.userApi

    private val _userInfo = MutableStateFlow<UserInfoResponse?>(null)
    val userInfo: StateFlow<UserInfoResponse?> = _userInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // 이미지 업로드 상태
    private val _imageUploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Initial)
    val imageUploadState: StateFlow<ImageUploadState> = _imageUploadState.asStateFlow()

    fun uploadProfileImage(context: Context, imageUri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _imageUploadState.value = ImageUploadState.Loading
            try {
                val imageFile = ImageUtils.uriToFile(context, imageUri)
                if (imageFile == null) {
                    _imageUploadState.value = ImageUploadState.Error("이미지 파일 변환에 실패했습니다")
                    return@launch
                }
                val resizedFile = ImageUtils.resizeImage(imageFile) ?: imageFile
                val imageUrl = S3Utils.uploadImageToS3(resizedFile, "profile")
                _imageUploadState.value = ImageUploadState.Success(imageUrl)
                onSuccess(imageUrl)
            } catch (e: Exception) {
                _imageUploadState.value = ImageUploadState.Error(e.message ?: "이미지 업로드 실패")
            }
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = tokenManager.getAccessToken().first()
                if (token == null) {
                    _error.value = "로그인이 필요합니다"
                    return@launch
                }
                val response = userApi.getUserInfo("Bearer $token")
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true && apiResponse.result != null) {
                        _userInfo.value = apiResponse.result
                    } else {
                        _error.value = apiResponse?.message ?: "유저 정보 조회 실패"
                    }
                } else {
                    _error.value = "서버 오류"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserInfo(nickname: String, birthDate: String, profileImageUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = tokenManager.getAccessToken().first()
                if (token == null) {
                    _error.value = "로그인이 필요합니다"
                    return@launch
                }
                val request = UserUpdateRequest(nickname, birthDate, profileImageUrl)
                val response = userApi.updateUserInfo("Bearer $token", request)
                if (response.isSuccessful) {
                    getUserInfo()
                } else {
                    _error.value = "정보 수정 실패"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// 이미지 업로드 상태 sealed class (AuthViewModel 참고)
sealed class ImageUploadState {
    object Initial : ImageUploadState()
    object Loading : ImageUploadState()
    data class Success(val imageUrl: String) : ImageUploadState()
    data class Error(val message: String) : ImageUploadState()
}
