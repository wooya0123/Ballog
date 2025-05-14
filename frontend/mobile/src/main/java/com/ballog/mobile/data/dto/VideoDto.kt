package com.ballog.mobile.data.dto

import android.net.Uri
import com.ballog.mobile.ui.video.HighlightUiState

data class VideoListResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: VideoListResult
)

data class VideoListResult(
    val totalQuarters: Int,
    val quarterList: List<VideoResponseDto>
)

data class VideoResponseDto(
    val videoId: Int?,
    val quarterNumber: Int?,
    val videoUrl: String?,
    val uploadSuccess: Boolean,
    val highlightList: List<HighlightDto>
)

data class HighlightDto(
    val highlightId: Int,
    val highlightName: String,
    val startTime: String,  // 예: "00:01:23"
    val endTime: String     // 예: "00:01:36"
)

data class PresignedVideoUploadRequest(
    val fileName: String
)

data class PresignedVideoUploadResponseWrapper(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: PresignedVideoUploadResponse
)

data class PresignedVideoUploadResponse(
    val s3Url: String
)

// 업로드 완료 후 영상 저장 요청 (기존 UploadSuccessRequest 대체)
data class SaveVideoRequest(
    val matchId: Int,
    val quarterNumber: Int,
    val duration: String,
    val videoUrl: String
)

data class HighlightAddRequest(
    val videoId: Int,
    val highlightName: String,
    val startTime: String,
    val endTime: String
)

data class HighlightUpdateRequest(
    val highlightId: Int,
    val highlightName: String,
    val startTime: String,
    val endTime: String
)

data class DeleteVideoRequest(
    val videoId: Int
)

data class DeleteHighlightRequest(
    val highlightId: Int
)

data class HighlightAddResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: HighlightAddResult
)

data class HighlightAddResult(
    val highlightId: Int
)

data class BaseResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Any? = null
)
