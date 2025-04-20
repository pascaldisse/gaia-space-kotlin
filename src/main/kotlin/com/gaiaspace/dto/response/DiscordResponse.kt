package com.gaiaspace.dto.response

import com.gaiaspace.domain.model.DiscordChannelMapping
import com.gaiaspace.domain.model.DiscordIntegration
import com.gaiaspace.domain.model.SyncDirection
import java.time.LocalDateTime

data class DiscordIntegrationResponse(
    val id: String,
    val workspaceId: String,
    val discordServerId: String,
    val discordServerName: String,
    val tokenExpiresAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val isActive: Boolean,
    val channelMappings: List<DiscordChannelMappingResponse>
) {
    companion object {
        fun fromDiscordIntegration(integration: DiscordIntegration): DiscordIntegrationResponse {
            return DiscordIntegrationResponse(
                id = integration.id,
                workspaceId = integration.workspace.id,
                discordServerId = integration.discordServerId,
                discordServerName = integration.discordServerName,
                tokenExpiresAt = integration.tokenExpiresAt,
                createdAt = integration.createdAt,
                updatedAt = integration.updatedAt,
                isActive = integration.isActive,
                channelMappings = integration.channelMappings.map { 
                    DiscordChannelMappingResponse.fromDiscordChannelMapping(it) 
                }
            )
        }
    }
}

data class DiscordChannelMappingResponse(
    val id: String,
    val discordChannelId: String,
    val discordChannelName: String,
    val gaiaChannelId: String,
    val gaiaChannelName: String,
    val syncEnabled: Boolean,
    val syncDirection: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromDiscordChannelMapping(mapping: DiscordChannelMapping): DiscordChannelMappingResponse {
            return DiscordChannelMappingResponse(
                id = mapping.id,
                discordChannelId = mapping.discordChannelId,
                discordChannelName = mapping.discordChannelName,
                gaiaChannelId = mapping.gaiaChannel.id,
                gaiaChannelName = mapping.gaiaChannel.name,
                syncEnabled = mapping.syncEnabled,
                syncDirection = mapping.syncDirection.name,
                createdAt = mapping.createdAt,
                updatedAt = mapping.updatedAt
            )
        }
    }
}

data class DiscordChannelResponse(
    val id: String,
    val name: String,
    val type: String
)