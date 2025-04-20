package com.gaiaspace.graphql

import com.gaiaspace.domain.model.Workspace
import com.gaiaspace.domain.model.WorkspaceMember
import com.gaiaspace.dto.request.CreateWorkspaceRequest
import com.gaiaspace.service.UserService
import com.gaiaspace.service.WorkspaceService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
class WorkspaceGraphQLController(
    private val workspaceService: WorkspaceService,
    private val userService: UserService
) {
    
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    fun workspaces(@Argument userId: String): List<Workspace> {
        return workspaceService.findByUser(userId)
    }
    
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    fun workspace(@Argument id: String): Workspace? {
        return workspaceService.findById(id)
    }
    
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    fun workspaceWithDetails(@Argument id: String): Workspace? {
        return workspaceService.findById(id)
    }
    
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    fun createWorkspace(
        @Argument userId: String,
        @Argument input: CreateWorkspaceRequest
    ): Workspace {
        return workspaceService.createWorkspace(
            name = input.name,
            description = input.description,
            createdBy = userId,
            avatarUrl = input.avatarUrl
        )
    }
    
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    fun updateWorkspace(
        @Argument id: String,
        @Argument name: String?,
        @Argument description: String?,
        @Argument avatarUrl: String?
    ): Workspace {
        return workspaceService.updateWorkspace(
            id = id,
            name = name,
            description = description,
            avatarUrl = avatarUrl
        ) ?: throw IllegalArgumentException("Workspace not found")
    }
    
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    fun deleteWorkspace(@Argument id: String): Boolean {
        return workspaceService.deleteWorkspace(id)
    }
    
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    fun addWorkspaceMember(
        @Argument workspaceId: String,
        @Argument userId: String,
        @Argument role: String
    ): WorkspaceMember {
        return workspaceService.addMember(
            workspaceId = workspaceId,
            userId = userId,
            role = role
        ) ?: throw IllegalArgumentException("Workspace or user not found")
    }
    
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    fun removeWorkspaceMember(
        @Argument workspaceId: String,
        @Argument userId: String
    ): Boolean {
        return workspaceService.removeMember(
            workspaceId = workspaceId,
            userId = userId
        )
    }
    
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    fun updateMemberRole(
        @Argument workspaceId: String,
        @Argument userId: String,
        @Argument role: String
    ): WorkspaceMember {
        return workspaceService.updateMemberRole(
            workspaceId = workspaceId,
            userId = userId,
            newRole = role
        ) ?: throw IllegalArgumentException("Member not found")
    }
}