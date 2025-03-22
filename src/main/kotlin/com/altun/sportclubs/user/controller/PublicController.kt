package com.altun.sportclubs.user.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.HashMap

@RestController
@RequestMapping("/api/public")
class PublicController {

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        val response = HashMap<String, Any>()
        response["status"] = "UP"
        response["message"] = "Application is running"
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/oauth2/login-options")
    fun loginOptions(): ResponseEntity<Map<String, Any>> {
        val response = HashMap<String, Any>()
        val options = ArrayList<Map<String, Any>>()
        
        val google = HashMap<String, Any>()
        google["provider"] = "google"
        google["url"] = "/oauth2/authorization/google"
        google["name"] = "Google"
        
        options.add(google)
        
        response["options"] = options
        return ResponseEntity.ok(response)
    }
} 