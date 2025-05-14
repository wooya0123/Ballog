package com.ballog.mobile.data.api

import androidx.lifecycle.viewModelScope
import com.ballog.mobile.data.dto.PlayerCardResponseDto
import com.ballog.mobile.data.dto.UserInfoResponse
import com.ballog.mobile.data.dto.UserStatisticsDto
import com.ballog.mobile.data.dto.UserUpdateRequest
import com.ballog.mobile.data.model.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface UserApi {

    @GET("v1/users")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserInfoResponse>>

    @PATCH("v1/users")
    suspend fun updateUserInfo(
        @Header ("Authorization") token: String,
        @Body request: UserUpdateRequest
    ): Response<ApiResponse<Unit>>

    // 홈화면 사용자 통계 정보 가져오기
    @GET("v1/users/statistics")
    suspend fun getUserStatistics(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserStatisticsDto>>

    // 선수 카드 조회
    @GET("v1/users/player-card")
    suspend fun getPlayerCard(
        @Header("Authorization") token: String
    ): Response<ApiResponse<PlayerCardResponseDto>>
}
