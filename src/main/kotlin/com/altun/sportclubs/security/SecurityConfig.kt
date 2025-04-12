package com.altun.sportclubs.security

import com.altun.sportclubs.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { userInfo ->
                        userInfo.userService(customOAuth2UserService)
                    }
                    .defaultSuccessUrl("http://localhost:3000/dashboard")
                    .failureUrl("/api/auth/login/failure")
            }
            .logout { logout ->
                logout
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessUrl("/api/auth/logout/success")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000") // Replace with your UI app URL
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}

@Configuration
class AuthenticationInterceptor(
    private val userService: UserService
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication

        // buna gerek var mi emin degilim public olanlari exclude ettim zatne
        val requestURI = request.requestURI
        if (isPublicEndpoint(requestURI)) {
            return true
        }

        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required")
            return false
        }

        // Additional checks for OAuth2 authentication
        if (authentication.principal is OAuth2User) {
            val principal = authentication.principal as OAuth2User
            try {
                // Validate and potentially refresh user
                userService.getOrCreateUserFromOAuth2(principal)
            } catch (e: Exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication")
                return false
            }
        }

        return true
    }

    private fun isPublicEndpoint(uri: String): Boolean {
        val publicPaths = setOf(
            "/api/public",
            "/api/auth/login",
            "/api/auth/logout/success",
            "/api/public/health",
            "/error",
            "/actuator/health"
        )

        return publicPaths.contains(uri) ||
                publicPaths.any { uri.startsWith(it) }
    }
}

@Configuration
class WebMvcConfig(
    private val authenticationInterceptor: AuthenticationInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/public/**",
                "/api/auth/login/**",
                "/error",
                "/actuator/health"
            )
    }
}