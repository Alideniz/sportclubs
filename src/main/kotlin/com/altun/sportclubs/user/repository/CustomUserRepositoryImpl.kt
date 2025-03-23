package com.altun.sportclubs.user.repository

import com.altun.sportclubs.user.model.User
import jakarta.persistence.EntityManager
import org.hibernate.Session
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Implementation of CustomUserRepository using Hibernate's NaturalId API
 * Based on Vlad Mihalcea's recommendations for efficient entity lookups
 */
@Repository
class CustomUserRepositoryImpl(private val entityManager: EntityManager) : CustomUserRepository {
    
    private val logger = LoggerFactory.getLogger(CustomUserRepositoryImpl::class.java)
    
    /**
     * Find a user by googleId using the NaturalId API
     * This uses a more efficient lookup strategy than a regular query
     * and takes advantage of Hibernate's natural ID cache
     */
    override fun findByGoogleId(googleId: String): Optional<User> {
        val session = entityManager.unwrap(Session::class.java)
        
        try {
            // Use loadByNaturalId for the most efficient lookup with cache support
            val user = session.byNaturalId(User::class.java)
                .using("googleId", googleId)
                .loadOptional()
            
            return if (user.isPresent) Optional.of(user.get()) else Optional.empty()
        } catch (e: Exception) {
            logger.error("Error finding user by googleId: ${e.message}", e)
            return Optional.empty()
        }
    }
} 