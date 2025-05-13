package com.ballog.mobile.data.api

import com.ballog.mobile.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface VideoApi {

    // 1. 쿼터별 영상 및 하이라이트 조회
    @GET("v1/videos/{matchId}")
    suspend fun getMatchVideos(
        @Path("matchId") matchId: Int
    ): Response<VideoListResponse>

    // 2. Presigned URL 요청 (쿼터 영상 업로드 시작)
    @POST("v1/videos")
    suspend fun requestUploadUrl(
        @Body request: PresignedVideoUploadRequest
    ): Response<PresignedVideoUploadResponseWrapper>

    // 3. 업로드 완료 알림
    @POST("v1/videos/status")
    suspend fun notifyUploadSuccess(
        @Body request: UploadSuccessRequest
    ): Response<BaseResponse>

    // 4. 쿼터 영상 삭제
    @DELETE("v1/videos")
    suspend fun deleteVideo(
        @Body request: DeleteVideoRequest
    ): Response<BaseResponse>

    // 5. 하이라이트 추가
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
    @DELETE("v1/videos/highlight")
    suspend fun deleteHighlight(
        @Body request: DeleteHighlightRequest
    ): Response<BaseResponse>
}
