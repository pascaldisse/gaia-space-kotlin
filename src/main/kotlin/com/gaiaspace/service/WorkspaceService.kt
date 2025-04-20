package com.gaiaspace.service

import com.gaiaspace.domain.model.User
import com.gaiaspace.domain.model.Workspace
import com.gaiaspace.domain.model.WorkspaceMember
import com.gaiaspace.domain.repository.UserRepository
import com.gaiaspace.domain.repository.WorkspaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class WorkspaceService(
    private val workspaceRepository: WorkspaceRepository,
    private val userRepository: UserRepository
) {
    fun findById(id: String): Workspace? {
        return workspaceRepository.findById(id).orElse(null)
    }
    
    fun findByUser(userId: String): List<Workspace> {
        return workspaceRepository.findByMemberId(userId)
    }
    
    fun searchWorkspaces(userId: String, searchTerm: String): List<Workspace> {
        return workspaceRepository.searchByNameForUser(userId, searchTerm)
    }
    
    @Transactional
    fun createWorkspace(name: String, description: String?, createdBy: String, avatarUrl: String?): Workspace {
        val creator = userRepository.findById(createdBy).orElseThrow { 
            IllegalArgumentException("User not found") 
        }
        
        val workspace = Workspace(
            name = name,
            description = description,
            createdBy = createdBy,
            avatarUrl = avatarUrl
        )
        
        val savedWorkspace = workspaceRepository.save(workspace)
        
        // Add creator as admin member
        val member = WorkspaceMember(
            workspace = savedWorkspace,
            user = creator,
            role = "ADMIN"
        )
        
        savedWorkspace.members.add(member)
        
        return workspaceRepository.save(savedWorkspace)
    }
    
    @Transactional
    fun updateWorkspace(id: String, name: String?, description: String?, avatarUrl: String?): Workspace? {
        val workspace = workspaceRepository.findById(id).orElse(null) ?: return null
        
        val updatedWorkspace = workspace.copy(
            name = name ?: workspace.name,
            description = description ?: workspace.description,
            avatarUrl = avatarUrl ?: workspace.avatarUrl,
            updatedAt = LocalDateTime.now()
        )
        
        return workspaceRepository.save(updatedWorkspace)
    }
    
    @Transactional
    fun addMember(workspaceId: String, userId: String, role: String): WorkspaceMember? {
        val workspace = workspaceRepository.findById(workspaceId).orElse(null) ?: return null
        val user = userRepository.findById(userId).orElse(null) ?: return null
        
        // Check if user is already a member
        if (workspace.members.any { it.user.id == userId }) {
            throw IllegalArgumentException("User is already a member of this workspace")
        }
        
        val member = WorkspaceMember(
            workspace = workspace,
            user = user,
            role = role
        )
        
        workspace.members.add(member)
        workspaceRepository.save(workspace)
        
        return member
    }
    
    @Transactional
    fun removeMember(workspaceId: String, userId: String): Boolean {
        val workspace = workspaceRepository.findById(workspaceId).orElse(null) ?: return false
        
        val memberToRemove = workspace.members.find { it.user.id == userId } ?: return false
        
        workspace.members.remove(memberToRemove)
        workspaceRepository.save(workspace)
        
        return true
    }
    
    @Transactional
    fun updateMemberRole(workspaceId: String, userId: String, newRole: String): WorkspaceMember? {
        val workspace = workspaceRepository.findById(workspaceId).orElse(null) ?: return null
        
        val memberIndex = workspace.members.indexOfFirst { it.user.id == userId }
        if (memberIndex == -1) return null
        
        val member = workspace.members[memberIndex]
        val updatedMember = member.copy(role = newRole)
        
        workspace.members[memberIndex] = updatedMember
        workspaceRepository.save(workspace)
        
        return updatedMember
    }
    
    @Transactional
    fun deleteWorkspace(id: String): Boolean {
        if (!workspaceRepository.existsById(id)) return false
        
        workspaceRepository.deleteById(id)
        return true
    }
}