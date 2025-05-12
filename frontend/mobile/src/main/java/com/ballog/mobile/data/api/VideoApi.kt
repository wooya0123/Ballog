package com.ballog.mobile.data.api

import com.ballog.mobile.data.dto.*
import com.ballog.mobile.data.model.ApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface VideoApi {

    @GET("api/v1/videos/{matchId}")
    suspend fun getMatchVideo(
        @Header("Authorization") token: String,
        @Path("matchId") matchId: Int
    ): Response<ApiResponse<VideoResponseDto>>

    @POST("/api/v1/videos")
    suspend fun getPresignedVideoUploadUrl(
        @Header("Authorization") token: String,
        @Body request: PresignedVideoUploadRequest
    ): Response<PresignedVideoUploadResponseWrapper>

    @DELETE("api/v1/videos")
    suspend fun deleteQuarterVideo(
        @Header("Authorization") token: String,
        @Query("videoId") videoId: Int
    ): Response<ApiResponse<Unit>>

    @POST("api/v1/videos/highlight")
    suspend fun addHighlight(
        @Header("Authorization") token: String,
        @Body request: HighlightAddRequest
    ): Response<ApiResponse<Unit>>

    @PATCH("api/v1/videos/highlight")
    suspend fun updateHighlight(
        @Header("Authorization") token: String,
        @Body request: HighlightUpdateRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("api/v1/videos/highlight")
    suspend fun deleteHighlight(
        @Header("Authorization") token: String,
        @Query("highlightId") highlightId: Int
    ): Response<ApiResponse<Unit>>
}
