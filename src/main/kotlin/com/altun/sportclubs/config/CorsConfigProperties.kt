package com.altun.sportclubs.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.cors")
open class CorsConfigProperties {
    var allowedOrigins: List<String> = listOf()
    var allowedMethods: List<String> = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
    var allowedHeaders: List<String> = listOf("*")
    var exposedHeaders: List<String> = listOf()
    var allowCredentials: Boolean = true
    var maxAge: Long = 3600
}