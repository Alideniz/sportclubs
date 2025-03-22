package com.altun.sportclubs.user.controller

import com.altun.sportclubs.user.dto.UserDTO
import com.altun.sportclubs.user.model.User
import com.altun.sportclubs.user.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/public/test")
class TestUserController(private val userRepository: UserRepository) {

    data class CreateUserRequest(
        val email: String,
        val name: String,
        val imageUrl: String? = null
    )

    @PostMapping("/users")
    fun createTestUser(@RequestBody request: CreateUserRequest): ResponseEntity<UserDTO> {
        // Check if email already exists
        if (userRepository.existsByEmail(request.email)) {
            return ResponseEntity.badRequest().build()
        }

        // Create a mock user as if it came from Google OAuth
        val user = User(
            email = request.email,
            name = request.name,
            imageUrl = request.imageUrl,
            googleId = "test-google-id-${UUID.randomUUID()}",
            enabled = true,
            createdAt = LocalDateTime.now(),
            lastLoginAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(user)
        
        return ResponseEntity.ok(
            UserDTO(
                id = savedUser.id,
                email = savedUser.email,
                name = savedUser.name,
                imageUrl = savedUser.imageUrl,
                createdAt = savedUser.createdAt,
                lastLoginAt = savedUser.lastLoginAt
            )
        )
    }

    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<UserDTO>> {
        val users = userRepository.findAll()
        val userDTOs = users.map { user ->
            UserDTO(
                id = user.id,
                email = user.email,
                name = user.name,
                imageUrl = user.imageUrl,
                createdAt = user.createdAt,
                lastLoginAt = user.lastLoginAt
            )
        }
        return ResponseEntity.ok(userDTOs)
    }
} 