package com.ballog.mobile.data.dto

data class UserInfoResponse(
    val email: String,
    val nickname: String,
    val birthDate: String,
    val profileImageUrl: String
)

data class UserUpdateRequest(
    val nickname: String,
    val birthDate: String,
    val profileImageUrl: String
)

fun UserInfoResponse.toUser(): com.ballog.mobile.data.model.User =
    com.ballog.mobile.data.model.User(
        email = this.email,
        nickname = this.nickname,
        birthDate = this.birthDate,
        profileImageUrl = this.profileImageUrl
    )

// 홈화면에 사용자 통계
data class UserStatisticsDto(
    val nickname: String,
    val heatmap: List<List<List<Int>>>,
    val distance: List<Double>,
    val speed: List<Double>,
    val sprint: List<Int>,
    val heartRate: List<Int>
)
