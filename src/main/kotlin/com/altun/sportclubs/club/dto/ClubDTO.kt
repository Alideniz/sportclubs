package com.altun.sportclubs.club.dto

import com.altun.sportclubs.club.model.Club
import java.time.LocalDateTime
import java.util.*

data class ClubDTO(
    val id: UUID,
    val name: String,
    val description: String?,
    val logoUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromEntity(club: Club): ClubDTO {
            return ClubDTO(
                id = club.id,
                name = club.name,
                description = club.description,
                logoUrl = club.logoUrl,
                createdAt = club.createdAt,
                updatedAt = club.updatedAt
            )
        }
    }
}