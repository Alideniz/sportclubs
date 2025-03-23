package com.altun.sportclubs.user.service

import com.altun.sportclubs.user.dto.UserDTO
import com.altun.sportclubs.user.model.User
import com.altun.sportclubs.user.repository.UserRepository
import org.hibernate.StaleObjectStateException
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.synchronized

@Service
class UserService(
    private val userRepository: UserRepository,
    private val transactionTemplate: TransactionTemplate
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    // Cache for storing processed GoogleIDs to prevent multiple simultaneous updates
    private val processingCache = HashMap<String, Boolean>()

    @Transactional(readOnly = true)
    fun findUserById(id: UUID): Optional<User> {
        return userRepository.findById(id)
    }

    @Transactional(readOnly = true)
    fun findUserByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    /**
     * Process the OAuth2 user with synchronization and retry logic to handle race conditions.
     * This method uses a processing cache to prevent duplicate processing and optimistic locking
     * with retries to resolve conflicts.
     */
    @Transactional
    fun processOAuth2User(oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes
        val googleId = attributes["sub"] as String

        // Check if we're already processing this Google ID
        synchronized(processingCache) {
            if (processingCache[googleId] == true) {
                // If already processing, just get the user without updating
                val userOptional = userRepository.findByGoogleId(googleId)
                if (userOptional.isPresent) {
                    return userOptional.get()
                }
                // If not found (rare), continue with processing
            }

            // Mark as being processed
            processingCache[googleId] = true
        }

        try {
            return retryOptimisticLock(3) {
                processUserInTransaction(oAuth2User)
            }
        } finally {
            // Remove from processing cache when done
            synchronized(processingCache) {
                processingCache.remove(googleId)
            }
        }
    }

    /**
     * Enhanced retry mechanism for optimistic locking failures with exponential backoff
     * Based on Vlad Mihalcea's recommendations for handling optimistic locking conflicts
     */
    private fun <T> retryOptimisticLock(maxRetries: Int, operation: () -> T): T {
        var lastException: Exception? = null
        
        for (attempt in 0 until maxRetries) {
            try {
                // Use programmatic transaction to have more control
                return if (attempt == 0) {
                    // First attempt without transaction template
                    operation()
                } else {
                    // Subsequent attempts with fresh transaction
                    transactionTemplate.execute { _ -> operation() }!!
                }
            } catch (e: Exception) {
                when (e) {
                    is OptimisticLockingFailureException, 
                    is ObjectOptimisticLockingFailureException,
                    is StaleObjectStateException -> {
                        lastException = e
                        
                        if (attempt < maxRetries - 1) {
                            // Use exponential backoff with jitter for retry
                            val backoffMillis = (2.0.pow(attempt.toDouble()) * 100).toLong()
                            val jitter = ThreadLocalRandom.current().nextLong(backoffMillis / 2)
                            val sleepTime = backoffMillis + jitter
                            
                            logger.warn("Optimistic locking failure on attempt ${attempt + 1}/$maxRetries: ${e.message}. Retrying in $sleepTime ms")
                            Thread.sleep(sleepTime)
                        } else {
                            logger.error("Final optimistic locking failure on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                        }
                    }
                    else -> throw e
                }
            }
        }
        
        throw lastException ?: RuntimeException("Failed after $maxRetries retries")
    }

    /**
     * The actual processing of the user within a transaction.
     * Refactored to use merge pattern for safer updates.
     */
    @Transactional
    fun processUserInTransaction(oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes
        val email = attributes["email"] as String
        val googleId = attributes["sub"] as String

        val existingUserOptional = userRepository.findByGoogleId(googleId)

        return if (existingUserOptional.isPresent) {
            // Use a merge pattern instead of creating a new entity instance
            val existingUser = existingUserOptional.get()
            
            // Update the existing user (safer than creating a new instance)
            existingUser.apply {
                // Since User is immutable, we need to create a new instance
                val updatedUser = User(
                    id = id,
                    email = email,
                    name = name,
                    imageUrl = imageUrl,
                    googleId = googleId,
                    enabled = enabled,
                    createdAt = createdAt,
                    updatedAt = LocalDateTime.now(),
                    lastLoginAt = LocalDateTime.now(),
                    version = version
                )
                return userRepository.save(updatedUser)
            }
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

    /**
     * Get user information without updating the database.
     * Used for endpoints that only need to retrieve user info.
     */
    @Transactional(readOnly = true)
    fun getOrCreateUserFromOAuth2(oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes
        val googleId = attributes["sub"] as String

        val existingUser = userRepository.findByGoogleId(googleId)

        return if (existingUser.isPresent) {
            existingUser.get()
        } else {
            // In the rare case user doesn't exist yet, create without transaction conflict
            processOAuth2User(oAuth2User)
        }
    }

    @Transactional(readOnly = true)
    fun getCurrentUserDTO(user: User): UserDTO {
        return UserDTO.fromEntity(user)
    }
} 