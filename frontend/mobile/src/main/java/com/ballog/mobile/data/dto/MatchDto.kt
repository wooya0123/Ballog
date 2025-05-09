package com.ballog.mobile.data.dto

// 매치 관련 요청/응답 DTO 정의 예정

// 개인 매치 리스트 조회 / 팀 매치 리스트 조회
data class MatchListResponse(
    val matchList: List<MatchItemDto>
)

data class MatchItemDto(
    val matchId: Int,
    val matchName: String,
    val matchDate: String,   // e.g., "2025-04-22"
    val startTime: String,   // e.g., "20:00"
    val endTime: String      // e.g., "22:00"
)

// 개인 매치 등록 요청
data class MatchRegisterRequest(
    val matchDate: String,   // yyyy-MM-dd
    val startTime: String,   // HH:mm
    val endTime: String,     // HH:mm
    val matchName: String
)

