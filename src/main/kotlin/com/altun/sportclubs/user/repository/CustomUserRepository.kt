package com.altun.sportclubs.user.repository

import com.altun.sportclubs.user.model.User
import java.util.Optional

/**
 * Custom repository interface for more efficient User entity operations
 * Based on Vlad Mihalcea's recommendations for NaturalId usage
 */
interface CustomUserRepository {
    
    /**
     * Find a user by its natural ID (googleId) using Hibernate's NaturalId API
     * This is more efficient than a regular JPQL query
     */
    fun findByGoogleId(googleId: String): Optional<User>
} 