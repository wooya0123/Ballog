package com.ballog.mobile.ui.video

import android.net.Uri

data class VideoUiState(
    val totalQuarters: Int = 1,
    val quarterList: List<QuarterVideoData> = emptyList()
)

data class QuarterVideoData(
    val videoUri: Uri? = null,
    val highlights: List<HighlightUiState> = emptyList(),
    val showPlayer: Boolean = false
)

data class HighlightUiState(
    val title: String,
    val startHour: String,
    val startMin: String,
    val endHour: String,
    val endMin: String
)
