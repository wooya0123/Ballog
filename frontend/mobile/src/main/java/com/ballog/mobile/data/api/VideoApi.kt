package com.ballog.mobile.data.api

import com.ballog.mobile.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface VideoApi {

    // 1. 경기 영상 조회
    @GET("v1/videos/{matchId}")
    suspend fun getMatchVideos(
        @Path("matchId") matchId: Int
    ): Response<VideoListResponse>

    // 2. Presigned URL 발급
    @POST("v1/videos/url")
    suspend fun requestUploadUrl(
        @Body request: PresignedVideoUploadRequest
    ): Response<PresignedVideoUploadResponseWrapper>

    // 3. 쿼터 영상 저장
    @POST("v1/videos")
    suspend fun saveVideo(
        @Body request: SaveVideoRequest
    ): Response<BaseResponse>

    // 4. 쿼터 영상 삭제
    @DELETE("v1/videos/{videoId}")
    suspend fun deleteVideo(
        @Path("videoId") videoId: Int
    ): Response<BaseResponse>

    // 5. 하이라이트 수동 추가
    @POST("v1/videos/highlight")
    suspend fun addHighlight(
        @Body request: HighlightAddRequest
    ): Response<HighlightAddResponse>

    // 6. 하이라이트 수정
    @PATCH("v1/videos/highlight")
    suspend fun updateHighlight(
        @Body request: HighlightUpdateRequest
    ): Response<BaseResponse>

    // 7. 하이라이트 삭제
    @DELETE("v1/videos/highlight/{highlightId}")
    suspend fun deleteHighlight(
        @Path("highlightId") highlightId: Int
    ): Response<BaseResponse>
}
