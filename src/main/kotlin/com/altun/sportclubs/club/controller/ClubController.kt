package com.altun.sportclubs.club.controller

import com.altun.sportclubs.club.dto.ClubDTO
import com.altun.sportclubs.club.model.ClubRole
import com.altun.sportclubs.club.service.ClubService
import com.altun.sportclubs.club.service.UserClubRoleService
import com.altun.sportclubs.user.dto.UserDTO
import com.altun.sportclubs.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/clubs")
class ClubController(
    private val clubService: ClubService,
    private val userClubRoleService: UserClubRoleService,
    private val userService: UserService
) {

    data class CreateClubRequest(
        val name: String,
        val description: String? = null,
        val logoUrl: String? = null
    )

    data class AssignRoleRequest(
        val userId: UUID,
        val role: ClubRole
    )

    @GetMapping
    fun getAllClubs(): ResponseEntity<List<ClubDTO>> {
        val clubs = clubService
            .findAll()
            .map { ClubDTO.fromEntity(it) }
        return ResponseEntity.ok(clubs)
    }

    @GetMapping("/{id}")
    fun getClub(@PathVariable id: UUID): ResponseEntity<ClubDTO> {
        val clubOpt = clubService.findById(id)
        return if (clubOpt.isPresent) {
            ResponseEntity.ok(ClubDTO.fromEntity(clubOpt.get()))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createClub(
        @RequestBody request: CreateClubRequest,
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<ClubDTO> {
        // Create the club
        val club = clubService.createClub(
            name = request.name,
            description = request.description,
            logoUrl = request.logoUrl
        )

        // Get current user
        val user = userService.getOrCreateUserFromOAuth2(principal)

        // Assign creator as ADMIN
        userClubRoleService.assignRoleToUser(user.id, club.id, ClubRole.ADMIN)

        return ResponseEntity.ok(ClubDTO.fromEntity(club))
    }

    @PutMapping("/{id}")
    fun updateClub(
        @PathVariable id: UUID,
        @RequestBody request: CreateClubRequest,
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<ClubDTO> {
        // Check if user is admin
        val user = userService.getOrCreateUserFromOAuth2(principal)
        if (!userClubRoleService.checkUserHasRole(user.id, id, ClubRole.ADMIN)) {
            return ResponseEntity.status(403).build()
        }

        val updatedClubOpt = clubService.updateClub(
            id = id,
            name = request.name,
            description = request.description,
            logoUrl = request.logoUrl
        )

        return if (updatedClubOpt.isPresent) {
            ResponseEntity.ok(ClubDTO.fromEntity(updatedClubOpt.get()))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteClub(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<Void> {
        // Check if user is admin
        val user = userService.getOrCreateUserFromOAuth2(principal)
        if (!userClubRoleService.checkUserHasRole(user.id, id, ClubRole.ADMIN)) {
            return ResponseEntity.status(403).build()
        }

        clubService.deleteClub(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/members")
    fun getClubMembers(
        @PathVariable id: UUID,
        @RequestParam(required = false) role: ClubRole?
    ): ResponseEntity<List<UserDTO>> {
        val members = userClubRoleService.getAllClubMembers(id, role)
            .map { UserDTO.fromEntity(it.user) }
        return ResponseEntity.ok(members)
    }

    @PostMapping("/{id}/members")
    fun assignRole(
        @PathVariable id: UUID,
        @RequestBody request: AssignRoleRequest,
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<Void> {
        // Check if user is admin
        val user = userService.getOrCreateUserFromOAuth2(principal)
        if (!userClubRoleService.checkUserHasRole(user.id, id, ClubRole.ADMIN)) {
            return ResponseEntity.status(403).build()
        }

        userClubRoleService.assignRoleToUser(request.userId, id, request.role)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}/members/{userId}")
    fun removeUserFromClub(
        @PathVariable id: UUID,
        @PathVariable userId: UUID,
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<Void> {
        // Check if user is admin
        val user = userService.getOrCreateUserFromOAuth2(principal)
        if (!userClubRoleService.checkUserHasRole(user.id, id, ClubRole.ADMIN)) {
            return ResponseEntity.status(403).build()
        }

        userClubRoleService.removeUserFromClub(userId, id)
        return ResponseEntity.noContent().build()
    }
}