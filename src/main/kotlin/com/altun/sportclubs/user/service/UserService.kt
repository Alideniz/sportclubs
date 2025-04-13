package com.altun.sportclubs.user.service

import com.altun.sportclubs.user.model.User
import com.altun.sportclubs.user.repository.UserRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    @Cacheable(value = ["userCache"], key = "#id")
    @Transactional(readOnly = true)
    fun findUserById(id: UUID): Optional<User> {
        return userRepository.findById(id)
    }

    @Transactional
    fun processOAuth2User(oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes
        val googleId = attributes["sub"] as String
        val email = attributes["email"] as String

        val existingUserOptional = userRepository.findUserByGoogleId(googleId)

        return if (existingUserOptional.isPresent) {
            return existingUserOptional.get()
        } else {
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
    fun getOrCreateUserFromOAuth2(oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes
        val googleId = attributes["sub"] as String

        val existingUser = userRepository.findUserByGoogleId(googleId)

        return if (existingUser.isPresent) {
            existingUser.get()
        } else {
            processOAuth2User(oAuth2User)
        }
    }
} 