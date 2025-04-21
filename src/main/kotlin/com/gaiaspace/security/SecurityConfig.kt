package com.gaiaspace.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .csrf { csrf -> csrf.disable() }
            // Configure headers to allow H2 console to display in iframe
            .headers { headers ->
                headers.frameOptions { frameOptions -> frameOptions.sameOrigin() }
            }
            .authorizeHttpRequests { authorize ->
                authorize
                    // Allow all static resources and paths
                    .requestMatchers("/**").permitAll()
                    // API endpoints that don't require auth
                    .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/docs/**").permitAll()
                    .requestMatchers("/api/projects/**").permitAll() // Allow projects API for demo
                    .requestMatchers("/api/public/**").permitAll() // Public API endpoints
                    .requestMatchers("/h2-console/**").permitAll() // Allow H2 console
                    // Only protect secured API endpoints
                    .requestMatchers("/api/workspaces/**", "/api/tasks/**", 
                                   "/api/pipelines/**", "/api/discord/**").authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        
        return http.build()
    }
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type")
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
    
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}