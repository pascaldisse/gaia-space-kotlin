package com.gaiaspace.controller

import com.gaiaspace.dto.request.AddMemberRequest
import com.gaiaspace.dto.request.CreateWorkspaceRequest
import com.gaiaspace.dto.request.UpdateMemberRoleRequest
import com.gaiaspace.dto.request.UpdateWorkspaceRequest
import com.gaiaspace.dto.response.WorkspaceMemberResponse
import com.gaiaspace.dto.response.WorkspaceResponse
import com.gaiaspace.service.WorkspaceService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/workspaces")
class WorkspaceController(
    private val workspaceService: WorkspaceService
) {
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getWorkspaces(@RequestAttribute("userId") userId: String): ResponseEntity<List<WorkspaceResponse>> {
        val workspaces = workspaceService.findByUser(userId)
        return ResponseEntity.ok(workspaces.map { WorkspaceResponse.fromWorkspace(it) })
    }
    
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    fun searchWorkspaces(
        @RequestAttribute("userId") userId: String,
        @RequestParam("q") searchTerm: String
    ): ResponseEntity<List<WorkspaceResponse>> {
        val workspaces = workspaceService.searchWorkspaces(userId, searchTerm)
        return ResponseEntity.ok(workspaces.map { WorkspaceResponse.fromWorkspace(it) })
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getWorkspaceById(@PathVariable id: String): ResponseEntity<WorkspaceResponse> {
        val workspace = workspaceService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(WorkspaceResponse.fromWorkspace(workspace))
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createWorkspace(
        @RequestAttribute("userId") userId: String,
        @Valid @RequestBody request: CreateWorkspaceRequest
    ): ResponseEntity<WorkspaceResponse> {
        val workspace = workspaceService.createWorkspace(
            name = request.name,
            description = request.description,
            createdBy = userId,
            avatarUrl = request.avatarUrl
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(WorkspaceResponse.fromWorkspace(workspace))
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun updateWorkspace(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateWorkspaceRequest
    ): ResponseEntity<WorkspaceResponse> {
        val workspace = workspaceService.updateWorkspace(
            id = id,
            name = request.name,
            description = request.description,
            avatarUrl = request.avatarUrl
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(WorkspaceResponse.fromWorkspace(workspace))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun deleteWorkspace(@PathVariable id: String): ResponseEntity<Void> {
        val success = workspaceService.deleteWorkspace(id)
        
        return if (success) ResponseEntity.noContent().build()
        else ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
    
    @GetMapping("/{id}/members")
    @PreAuthorize("isAuthenticated()")
    fun getWorkspaceMembers(@PathVariable id: String): ResponseEntity<List<WorkspaceMemberResponse>> {
        val workspace = workspaceService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(
            workspace.members.map { WorkspaceMemberResponse.fromWorkspaceMember(it) }
        )
    }
    
    @PostMapping("/{id}/members")
    @PreAuthorize("isAuthenticated()")
    fun addMember(
        @PathVariable id: String,
        @Valid @RequestBody request: AddMemberRequest
    ): ResponseEntity<WorkspaceMemberResponse> {
        val member = workspaceService.addMember(
            workspaceId = id,
            userId = request.userId,
            role = request.role
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(WorkspaceMemberResponse.fromWorkspaceMember(member))
    }
    
    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("isAuthenticated()")
    fun removeMember(
        @PathVariable id: String,
        @PathVariable userId: String
    ): ResponseEntity<Void> {
        val success = workspaceService.removeMember(id, userId)
        
        return if (success) ResponseEntity.noContent().build()
        else ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
    
    @PutMapping("/{id}/members/{userId}/role")
    @PreAuthorize("isAuthenticated()")
    fun updateMemberRole(
        @PathVariable id: String,
        @PathVariable userId: String,
        @Valid @RequestBody request: UpdateMemberRoleRequest
    ): ResponseEntity<WorkspaceMemberResponse> {
        val member = workspaceService.updateMemberRole(
            workspaceId = id,
            userId = userId,
            newRole = request.role
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(WorkspaceMemberResponse.fromWorkspaceMember(member))
    }
}