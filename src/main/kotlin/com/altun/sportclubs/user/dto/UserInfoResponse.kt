package com.altun.sportclubs.user.dto

data class UserInfoResponse(
    val user: UserDTO,
    val isAuthenticated: Boolean = true
)