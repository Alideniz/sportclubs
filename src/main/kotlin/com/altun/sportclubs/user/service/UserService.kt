package com.altun.sportclubs.user.service

import com.altun.sportclubs.user.dto.UserDTO
import com.altun.sportclubs.user.model.User
import com.altun.sportclubs.user.repository.UserRepository
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional(readOnly = true)
    fun findUserById(id: UUID): Optional<User> {
        return userRepository.findById(id)
    }

    @Transactional(readOnly = true)
    fun findUserByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    @Transactional
    fun processOAuth2User(oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes
        val email = attributes["email"] as String
        val googleId = attributes["sub"] as String
        
        val existingUser = userRepository.findByGoogleId(googleId)
        
        return if (existingUser.isPresent) {
            // Update existing user's last login time
            val user = existingUser.get()
            val updatedUser = User(
                id = user.id,
                email = user.email,
                name = user.name,
                imageUrl = user.imageUrl,
                googleId = user.googleId,
                enabled = user.enabled,
                createdAt = user.createdAt,
                updatedAt = LocalDateTime.now(),
                lastLoginAt = LocalDateTime.now()
            )
            userRepository.save(updatedUser)
        } else {
            // Create new user
            val name = attributes["name"] as String
            val imageUrl = attributes["picture"] as? String
            
            val newUser = User(
                email = email,
                name = name,
                imageUrl = imageUrl,
                googleId = googleId,
                enabled = true
            )
            userRepository.save(newUser)
        }
    }

    @Transactional(readOnly = true)
    fun getCurrentUserDTO(user: User): UserDTO {
        return UserDTO.fromEntity(user)
    }
} 