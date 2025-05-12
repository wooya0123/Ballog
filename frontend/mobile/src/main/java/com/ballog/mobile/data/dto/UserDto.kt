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

