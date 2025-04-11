package com.altun.sportclubs.club.service

import com.altun.sportclubs.club.model.ClubRole
import com.altun.sportclubs.club.model.UserClubRole
import com.altun.sportclubs.club.repository.ClubRepository
import com.altun.sportclubs.club.repository.UserClubRoleRepository
import com.altun.sportclubs.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class UserClubRoleService(
    private val userClubRoleRepository: UserClubRoleRepository,
    private val userRepository: UserRepository,
    private val clubRepository: ClubRepository
) {

    @Transactional(readOnly = true)
    fun getUserRoleInClub(userId: UUID, clubId: UUID): Optional<UserClubRole> {
        return userClubRoleRepository.findByUserIdAndClubId(userId, clubId)
    }

    @Transactional(readOnly = true)
    fun getAllUserClubRoles(userId: UUID): List<UserClubRole> {
        return userClubRoleRepository.findAllByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun getAllClubMembers(clubId: UUID, role: ClubRole? = null): List<UserClubRole> {
        return if (role != null) {
            userClubRoleRepository.findAllByClubIdAndRole(clubId, role)
        } else {
            userClubRoleRepository.findAll().filter { it.club.id == clubId }
        }
    }

    @Transactional
    fun assignRoleToUser(userId: UUID, clubId: UUID, role: ClubRole): UserClubRole {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with id: $userId") }

        val club = clubRepository.findById(clubId)
            .orElseThrow { IllegalArgumentException("Club not found with id: $clubId") }

        // Check if user already has a role in this club
        val existingRole = userClubRoleRepository.findByUserAndClub(user, club)

        return if (existingRole.isPresent) {
            // Update existing role
            val current = existingRole.get()
            val updated = UserClubRole(
                id = current.id,
                user = current.user,
                club = current.club,
                role = role,
                joinedAt = current.joinedAt,
                updatedAt = LocalDateTime.now(),
                version = current.version
            )
            userClubRoleRepository.save(updated)
        } else {
            // Create new role assignment
            val userClubRole = UserClubRole(
                user = user,
                club = club,
                role = role
            )
            userClubRoleRepository.save(userClubRole)
        }
    }

    @Transactional
    fun removeUserFromClub(userId: UUID, clubId: UUID) {
        val roleOpt = userClubRoleRepository.findByUserIdAndClubId(userId, clubId)
        roleOpt.ifPresent { userClubRoleRepository.delete(it) }
    }

    @Transactional(readOnly = true)
    fun checkUserHasRole(userId: UUID, clubId: UUID, role: ClubRole): Boolean {
        val roleOpt = userClubRoleRepository.findByUserIdAndClubId(userId, clubId)
        return roleOpt.map { it.role == role }.orElse(false)
    }
}