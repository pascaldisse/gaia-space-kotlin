package com.gaiaspace.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateWorkspaceRequest(
    @field:NotBlank(message = "Workspace name is required")
    @field:Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    val name: String,
    
    val description: String? = null,
    
    val avatarUrl: String? = null
)

data class UpdateWorkspaceRequest(
    val name: String? = null,
    
    val description: String? = null,
    
    val avatarUrl: String? = null
)

data class AddMemberRequest(
    @field:NotBlank(message = "User ID is required")
    val userId: String,
    
    @field:NotBlank(message = "Role is required")
    val role: String
)

data class UpdateMemberRoleRequest(
    @field:NotBlank(message = "Role is required")
    val role: String
)