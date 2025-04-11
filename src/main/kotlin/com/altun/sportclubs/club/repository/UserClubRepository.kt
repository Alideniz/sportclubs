package com.altun.sportclubs.club.repository

import com.altun.sportclubs.club.model.Club
import com.altun.sportclubs.club.model.ClubRole
import com.altun.sportclubs.club.model.UserClubRole
import com.altun.sportclubs.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface UserClubRoleRepository : JpaRepository<UserClubRole, UUID> {

    fun findByUserAndClub(user: User, club: Club): Optional<UserClubRole>

    @Query("SELECT ucr FROM UserClubRole ucr WHERE ucr.user.id = :userId AND ucr.club.id = :clubId")
    fun findByUserIdAndClubId(@Param("userId") userId: UUID, @Param("clubId") clubId: UUID): Optional<UserClubRole>

    @Query("SELECT ucr FROM UserClubRole ucr WHERE ucr.user.id = :userId")
    fun findAllByUserId(@Param("userId") userId: UUID): List<UserClubRole>

    @Query("SELECT ucr FROM UserClubRole ucr WHERE ucr.club.id = :clubId AND ucr.role = :role")
    fun findAllByClubIdAndRole(@Param("clubId") clubId: UUID, @Param("role") role: ClubRole): List<UserClubRole>
}