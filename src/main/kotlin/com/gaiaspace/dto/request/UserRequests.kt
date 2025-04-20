package com.gaiaspace.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String,
    
    @field:NotBlank(message = "Display name is required")
    val displayName: String
)

data class LoginRequest(
    @field:NotBlank(message = "Username or email is required")
    val usernameOrEmail: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class UpdateUserRequest(
    val displayName: String? = null,
    val avatarUrl: String? = null
)

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val newPassword: String
)