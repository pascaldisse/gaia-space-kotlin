package com.gaiaspace.controller

import com.gaiaspace.dto.request.ChangePasswordRequest
import com.gaiaspace.dto.request.LoginRequest
import com.gaiaspace.dto.request.RegisterRequest
import com.gaiaspace.dto.request.UpdateUserRequest
import com.gaiaspace.dto.response.UserResponse
import com.gaiaspace.security.JwtTokenProvider
import com.gaiaspace.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val user = userService.createUser(
            username = request.username,
            email = request.email,
            password = request.password,
            displayName = request.displayName
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromUser(user))
    }
    
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Map<String, Any>> {
        val user = userService.authenticateUser(request.usernameOrEmail, request.password)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        
        val token = jwtTokenProvider.generateToken(user)
        
        val response = mapOf(
            "token" to token,
            "user" to UserResponse.fromUser(user)
        )
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(@RequestAttribute("userId") userId: String): ResponseEntity<UserResponse> {
        val user = userService.findById(userId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(UserResponse.fromUser(user))
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserResponse> {
        val user = userService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(UserResponse.fromUser(user))
    }
    
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun updateUser(
        @RequestAttribute("userId") userId: String,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val updatedUser = userService.updateUser(
            id = userId,
            displayName = request.displayName,
            avatarUrl = request.avatarUrl
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(UserResponse.fromUser(updatedUser))
    }
    
    @PostMapping("/me/change-password")
    @PreAuthorize("isAuthenticated()")
    fun changePassword(
        @RequestAttribute("userId") userId: String,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<Void> {
        val success = userService.changePassword(
            id = userId,
            currentPassword = request.currentPassword,
            newPassword = request.newPassword
        )
        
        return if (success) ResponseEntity.ok().build()
        else ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    }
}