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

