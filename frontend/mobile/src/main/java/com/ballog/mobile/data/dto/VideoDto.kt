package com.ballog.mobile.data.dto

data class VideoResponseDto(
    val videoId: Int,
    val videoUrl: String,
    val quarter: Int,
    val highlightList: List<HighlightDto>
)

data class HighlightDto(
    val highlightId: Int,
    val title: String,
    val startTime: String,  // 예: "00:35"
    val endTime: String     // 예: "00:50"
)

data class HighlightAddRequest(
    val videoId: Int,
    val title: String,
    val startTime: String,
    val endTime: String
)

data class HighlightUpdateRequest(
    val highlightId: Int,
    val title: String,
    val startTime: String,
    val endTime: String
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
    val url: String
)
