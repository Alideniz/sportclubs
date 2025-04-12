package com.altun.sportclubs.user.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public")
class PublicController {

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf(
                "status" to "UP",
                "message" to "Application is running"
            )
        )
    }

    @GetMapping("/oauth2/login-options")
    fun loginOptions(): ResponseEntity<Map<String, Any>> {
        val response = mapOf(
            "options" to mapOf(
                "provider" to "google",
                "url" to "/oauth2/authorization/google",
                "name" to "Google",
            )
        )
        return ResponseEntity.ok(response)
    }
} 