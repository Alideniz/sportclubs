package com.altun.sportclubs.club.repository

import com.altun.sportclubs.club.model.Club
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ClubRepository : JpaRepository<Club, UUID> {
    fun findByName(name: String): Optional<Club>
}