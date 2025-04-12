package com.altun.sportclubs.user.controller

import com.altun.sportclubs.user.dto.UserDTO
import com.altun.sportclubs.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal principal: OAuth2User): ResponseEntity<UserDTO> {
        val user = userService.processOAuth2User(principal)
        return ResponseEntity.ok(UserDTO.fromEntity(user))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserDTO> {
        val userOptional = userService.findUserById(id)

        return if (userOptional.isPresent) {
            ResponseEntity.ok(UserDTO.fromEntity(userOptional.get()))
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 