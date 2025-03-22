package com.altun.sportclubs.user.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @Column(nullable = false, unique = true)
    val email: String,
    
    @Column(nullable = false)
    val name: String,
    
    val imageUrl: String? = null,
    
    @Column(nullable = false)
    val googleId: String,
    
    @Column(nullable = false)
    val enabled: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    val lastLoginAt: LocalDateTime = LocalDateTime.now()
) 