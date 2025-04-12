package com.altun.sportclubs.user.dto

import com.altun.sportclubs.user.model.User
import java.time.LocalDateTime
import java.util.*

data class UserDTO(
    val id: UUID,
    val email: String,
    val name: String,
    val imageUrl: String? = null,
    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime
) {
    companion object {
        fun fromEntity(user: User): UserDTO {
            return UserDTO(
                id = user.id,
                email = user.email,
                name = user.name,
                imageUrl = user.imageUrl,
                createdAt = user.createdAt,
                lastLoginAt = user.lastLoginAt
            )
        }
    }

}

data class UserInfoResponse(
    val user: UserDTO,
    val isAuthenticated: Boolean = true
) 