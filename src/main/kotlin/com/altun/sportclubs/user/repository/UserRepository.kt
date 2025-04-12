package com.altun.sportclubs.user.repository

import com.altun.sportclubs.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    fun findByEmail(@Param("email") email: String): Optional<User>

    @Query("SELECT u FROM User u WHERE u.googleId = :googleId")
    fun findUserByGoogleId(@Param("googleId") googleId: String): Optional<User>
} 