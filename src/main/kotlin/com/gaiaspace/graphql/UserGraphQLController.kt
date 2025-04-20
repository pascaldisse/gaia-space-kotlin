package com.gaiaspace.graphql

import com.gaiaspace.domain.model.User
import com.gaiaspace.dto.request.LoginRequest
import com.gaiaspace.dto.request.RegisterRequest
import com.gaiaspace.security.JwtTokenProvider
import com.gaiaspace.service.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import java.util.HashMap

@Controller
class UserGraphQLController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    fun me(@Argument userId: String): User? {
        return userService.findById(userId)
    }
    
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    fun user(@Argument id: String): User? {
        return userService.findById(id)
    }
    
    @MutationMapping
    fun register(@Argument input: RegisterRequest): User {
        return userService.createUser(
            username = input.username,
            email = input.email,
            password = input.password,
            displayName = input.displayName
        )
    }
    
    @MutationMapping
    fun login(@Argument input: LoginRequest): Map<String, Any> {
        val user = userService.authenticateUser(input.usernameOrEmail, input.password)
            ?: throw IllegalArgumentException("Invalid credentials")
        
        val token = jwtTokenProvider.generateToken(user)
        
        return mapOf(
            "token" to token,
            "user" to user
        )
    }
    
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    fun updateUser(
        @Argument userId: String,
        @Argument displayName: String?,
        @Argument avatarUrl: String?
    ): User {
        return userService.updateUser(
            id = userId,
            displayName = displayName,
            avatarUrl = avatarUrl
        ) ?: throw IllegalArgumentException("User not found")
    }
    
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    fun changePassword(
        @Argument userId: String,
        @Argument currentPassword: String,
        @Argument newPassword: String
    ): Boolean {
        return userService.changePassword(
            id = userId,
            currentPassword = currentPassword,
            newPassword = newPassword
        )
    }
}