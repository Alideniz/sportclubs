package com.altun.sportclubs.user.controller

import com.altun.sportclubs.user.dto.UserDTO
import com.altun.sportclubs.user.dto.UserInfoResponse
import com.altun.sportclubs.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.HashMap

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService) {

    @GetMapping("/login/success")
    fun loginSuccess(@AuthenticationPrincipal principal: OAuth2User): ResponseEntity<UserInfoResponse> {
        val user = userService.processOAuth2User(principal)
        val userDTO = UserDTO.fromEntity(user)
        return ResponseEntity.ok(UserInfoResponse(userDTO))
    }

    @GetMapping("/login/failure")
    fun loginFailure(): ResponseEntity<Map<String, Any>> {
        val response = HashMap<String, Any>()
        response["success"] = false
        response["message"] = "Login failed"
        return ResponseEntity.badRequest().body(response)
    }

    @GetMapping("/logout/success")
    fun logoutSuccess(): ResponseEntity<Map<String, Any>> {
        val response = HashMap<String, Any>()
        response["success"] = true
        response["message"] = "Logout successful"
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user")
    fun getCurrentUser(@AuthenticationPrincipal principal: OAuth2User?): ResponseEntity<*> {
        return if (principal != null) {
            val user = userService.processOAuth2User(principal)
            val userDTO = UserDTO.fromEntity(user)
            ResponseEntity.ok(UserInfoResponse(userDTO))
        } else {
            val response = HashMap<String, Any>()
            response["isAuthenticated"] = false
            ResponseEntity.ok(response)
        }
    }
} 