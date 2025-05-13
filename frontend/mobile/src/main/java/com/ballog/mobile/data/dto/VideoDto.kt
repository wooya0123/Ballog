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
    val quarterNumber: Int,
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
    val matchId: Int,
    val quarterNumber: Int,
    val duration: String,
    val fileName: String
)

data class PresignedVideoUploadResponseWrapper(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: PresignedVideoUploadResponse
)

data class PresignedVideoUploadResponse(
    val videoUrl: String
)

data class UploadSuccessRequest(
    val matchId: Int,
    val quarterNumber: Int
)

data class DeleteVideoRequest(
    val videoId: Int
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

data class VideoUiState(
    val totalQuarters: Int = 1,
    val quarterList: List<QuarterVideoData> = emptyList()
)

data class QuarterVideoData(
    val videoUri: Uri? = null,
    val highlights: List<HighlightUiState> = emptyList(),
    val showPlayer: Boolean = false
)
