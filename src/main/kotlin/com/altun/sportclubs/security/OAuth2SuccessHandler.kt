package com.altun.sportclubs.security

import com.altun.sportclubs.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler(private val userService: UserService) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // The user is already processed in CustomOAuth2UserService, no need to process again here
        // to avoid "Row was updated or deleted by another transaction" exception
        
        // Set default target URL to redirect to the frontend dashboard
        defaultTargetUrl = "http://localhost:3000/dashboard"
        
        // Handle the redirect using the parent class method
        super.onAuthenticationSuccess(request, response, authentication)
    }
} 