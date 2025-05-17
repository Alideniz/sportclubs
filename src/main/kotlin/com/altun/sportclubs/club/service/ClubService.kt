package com.altun.sportclubs.club.service

import com.altun.sportclubs.club.model.Club
import com.altun.sportclubs.club.repository.ClubRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
open class ClubService(private val clubRepository: ClubRepository) {

    @Transactional(readOnly = true)
    open fun findById(id: UUID): Optional<Club> {
        return clubRepository.findById(id)
    }

    @Transactional(readOnly = true)
    open fun findAll(): List<Club> {
        return clubRepository.findAll()
    }

    @Transactional
    open fun createClub(name: String, description: String? = null, logoUrl: String? = null): Club {
        val club = Club(
            name = name,
            description = description,
            logoUrl = logoUrl
        )
        return clubRepository.save(club)
    }

    @Transactional
    open fun updateClub(id: UUID, name: String, description: String? = null, logoUrl: String? = null): Optional<Club> {
        val clubOptional = clubRepository.findById(id)

        if (clubOptional.isPresent) {
            val existingClub = clubOptional.get()
            val updatedClub = Club(
                id = existingClub.id,
                name = name,
                description = description,
                logoUrl = logoUrl,
                createdAt = existingClub.createdAt,
                updatedAt = LocalDateTime.now(),
                version = existingClub.version
            )
            return Optional.of(clubRepository.save(updatedClub))
        }

        return Optional.empty()
    }

    @Transactional
    open fun deleteClub(id: UUID) {
        clubRepository.deleteById(id)
    }
}