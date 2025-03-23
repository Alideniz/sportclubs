package com.altun.sportclubs.user.repository

import com.altun.sportclubs.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID>, CustomUserRepository {

    /**
     * Find user by email with query cache
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    fun findByEmail(@Param("email") email: String): Optional<User>

    /**
     * Check if user exists by email
     */
    fun existsByEmail(email: String): Boolean
} 