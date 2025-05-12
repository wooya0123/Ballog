package com.ballog.mobile.data.model

import com.ballog.mobile.data.dto.HighlightDto
import com.ballog.mobile.data.dto.VideoResponseDto

// 쿼터 영상 단위 모델
data class Video(
    val id: Int,
    val url: String,
    val quarter: Int,
    val highlights: List<Highlight>
)

// 하이라이트 구간 모델
data class Highlight(
    val id: Int,
    val title: String,
    val startTime: String,
    val endTime: String
)

// 확장 함수: DTO → Model 변환
fun VideoResponseDto.toVideo() = Video(
    id = videoId,
    url = videoUrl,
    quarter = quarter,
    highlights = highlightList.map { it.toHighlight() }
)

fun HighlightDto.toHighlight() = Highlight(
    id = highlightId,
    title = title,
    startTime = startTime,
    endTime = endTime
)
