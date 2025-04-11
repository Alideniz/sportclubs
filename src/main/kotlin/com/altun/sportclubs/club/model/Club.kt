package com.altun.sportclubs.club.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "clubs")
class Club(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true, length = 1000)
    val description: String? = null,

    @Column(nullable = true)
    val logoUrl: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    val updatedAt: LocalDateTime? = null,

    @Version
    val version: Long = 0
)