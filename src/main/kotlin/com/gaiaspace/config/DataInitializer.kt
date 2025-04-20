package com.gaiaspace.config

import com.gaiaspace.domain.model.User
import com.gaiaspace.domain.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

@Configuration
class DataInitializer {

    @Bean
    @Profile("dev")
    fun initializeData(userRepository: UserRepository, passwordEncoder: PasswordEncoder): CommandLineRunner {
        return CommandLineRunner {
            // Create a test admin user if it doesn't exist
            if (!userRepository.existsByUsername("admin")) {
                val adminUser = User(
                    username = "admin",
                    email = "admin@example.com",
                    displayName = "Admin User",
                    password = passwordEncoder.encode("password"),
                    createdAt = LocalDateTime.now(),
                    roles = setOf("ADMIN", "USER")
                )
                userRepository.save(adminUser)
                println("Created admin user: ${adminUser.username}")
            }
            
            // Create a test user if it doesn't exist
            if (!userRepository.existsByUsername("user")) {
                val regularUser = User(
                    username = "user",
                    email = "user@example.com",
                    displayName = "Regular User",
                    password = passwordEncoder.encode("password"),
                    createdAt = LocalDateTime.now(),
                    roles = setOf("USER")
                )
                userRepository.save(regularUser)
                println("Created regular user: ${regularUser.username}")
            }
        }
    }
}