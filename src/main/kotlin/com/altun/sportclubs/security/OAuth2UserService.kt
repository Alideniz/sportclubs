package com.altun.sportclubs.security

import com.altun.sportclubs.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(private val userService: UserService) : DefaultOAuth2UserService() {
    private val logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        try {
            userService.processOAuth2User(oAuth2User)
        } catch (e: Exception) {
            logger.error("Error processing OAuth2 user: ${e.message}", e)
        }

        return oAuth2User
    }
} 