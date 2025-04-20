package com.gaiaspace.service

import com.gaiaspace.domain.model.User
import com.gaiaspace.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun findById(id: String): User? {
        return userRepository.findById(id).orElse(null)
    }
    
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username).orElse(null)
    }
    
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }
    
    @Transactional
    fun createUser(username: String, email: String, password: String, displayName: String): User {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }
        
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already exists")
        }
        
        val user = User(
            username = username,
            email = email,
            password = passwordEncoder.encode(password),
            displayName = displayName
        )
        
        return userRepository.save(user)
    }
    
    @Transactional
    fun updateUser(id: String, displayName: String?, avatarUrl: String?): User? {
        val existingUser = userRepository.findById(id).orElse(null) ?: return null
        
        val updatedUser = existingUser.copy(
            displayName = displayName ?: existingUser.displayName,
            avatarUrl = avatarUrl ?: existingUser.avatarUrl,
            updatedAt = LocalDateTime.now()
        )
        
        return userRepository.save(updatedUser)
    }
    
    @Transactional
    fun changePassword(id: String, currentPassword: String, newPassword: String): Boolean {
        val user = userRepository.findById(id).orElse(null) ?: return false
        
        if (!passwordEncoder.matches(currentPassword, user.password)) {
            return false
        }
        
        val updatedUser = user.copy(
            password = passwordEncoder.encode(newPassword),
            updatedAt = LocalDateTime.now()
        )
        
        userRepository.save(updatedUser)
        return true
    }
    
    fun authenticateUser(usernameOrEmail: String, password: String): User? {
        val user = userRepository.findByUsername(usernameOrEmail).orElse(null)
            ?: userRepository.findByEmail(usernameOrEmail).orElse(null)
            ?: return null
        
        return if (passwordEncoder.matches(password, user.password)) user else null
    }
}