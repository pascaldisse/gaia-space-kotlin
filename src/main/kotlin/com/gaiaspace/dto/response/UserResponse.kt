package com.gaiaspace.dto.response

import com.gaiaspace.domain.model.User
import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val roles: Set<String>
) {
    companion object {
        fun fromUser(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                displayName = user.displayName,
                avatarUrl = user.avatarUrl,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                roles = user.roles
            )
        }
    }
}