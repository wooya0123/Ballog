package com.ballog.mobile.data.api

import com.ballog.mobile.data.dto.UserInfoResponse
import com.ballog.mobile.data.dto.UserUpdateRequest
import com.ballog.mobile.data.model.ApiResponse
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

}
