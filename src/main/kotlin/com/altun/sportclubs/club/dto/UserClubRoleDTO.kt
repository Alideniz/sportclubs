package com.altun.sportclubs.club.dto

import com.altun.sportclubs.club.model.UserClubRole
import java.time.LocalDateTime

data class UserClubRoleDTO(
    val club: ClubDTO,
    val role: String,
    val joinedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(userClubRole: UserClubRole): UserClubRoleDTO {
            return UserClubRoleDTO(
                club = ClubDTO.fromEntity(userClubRole.club),
                role = userClubRole.role.name,
                joinedAt = userClubRole.joinedAt,
            )
        }
    }
}