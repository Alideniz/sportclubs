package com.altun.sportclubs.security

import com.altun.sportclubs.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler(private val userService: UserService) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // Process the user on successful authentication
        val oAuth2User = authentication.principal as OAuth2User
        userService.processOAuth2User(oAuth2User)
        
        // Set default target URL
        defaultTargetUrl = "/api/auth/login/success"
        
        // Handle the redirect using the parent class method
        super.onAuthenticationSuccess(request, response, authentication)
    }
} 