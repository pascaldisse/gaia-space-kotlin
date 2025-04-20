package com.gaiaspace.dto.response

import com.gaiaspace.domain.model.Workspace
import com.gaiaspace.domain.model.WorkspaceMember
import java.time.LocalDateTime

data class WorkspaceResponse(
    val id: String,
    val name: String,
    val description: String?,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val avatarUrl: String?,
    val membersCount: Int,
    val channelsCount: Int
) {
    companion object {
        fun fromWorkspace(workspace: Workspace): WorkspaceResponse {
            return WorkspaceResponse(
                id = workspace.id,
                name = workspace.name,
                description = workspace.description,
                createdBy = workspace.createdBy,
                createdAt = workspace.createdAt,
                updatedAt = workspace.updatedAt,
                avatarUrl = workspace.avatarUrl,
                membersCount = workspace.members.size,
                channelsCount = workspace.channels.size
            )
        }
    }
}

data class WorkspaceMemberResponse(
    val id: String,
    val userId: String,
    val username: String,
    val displayName: String,
    val role: String,
    val joinedAt: LocalDateTime
) {
    companion object {
        fun fromWorkspaceMember(member: WorkspaceMember): WorkspaceMemberResponse {
            return WorkspaceMemberResponse(
                id = member.id,
                userId = member.user.id,
                username = member.user.username,
                displayName = member.user.displayName,
                role = member.role,
                joinedAt = member.joinedAt
            )
        }
    }
}