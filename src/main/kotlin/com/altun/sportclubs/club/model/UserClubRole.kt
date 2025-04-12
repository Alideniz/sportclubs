package com.altun.sportclubs.club.model


import com.altun.sportclubs.user.model.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "user_club_roles",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "club_id"])]
)
class UserClubRole(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    val club: Club,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: ClubRole,

    @Column(nullable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    val updatedAt: LocalDateTime? = null,

    @Version
    val version: Long = 0
)