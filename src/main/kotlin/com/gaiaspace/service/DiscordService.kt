package com.gaiaspace.service

import com.gaiaspace.domain.model.*
import com.gaiaspace.domain.repository.ChannelRepository
import com.gaiaspace.domain.repository.DiscordIntegrationRepository
import com.gaiaspace.domain.repository.WorkspaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class DiscordService(
    private val discordIntegrationRepository: DiscordIntegrationRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val channelRepository: ChannelRepository
) {
    // Commented out while fixing Discord4J integration issues
    // private val clientId: String,
    // private val clientSecret: String,
    // private val redirectUri: String
    
    @Transactional
    fun createIntegration(
        workspaceId: String,
        discordServerId: String,
        discordServerName: String,
        accessToken: String,
        refreshToken: String,
        expiresIn: Long,
        userId: String
    ): DiscordIntegration {
        val workspace = workspaceRepository.findById(workspaceId).orElseThrow {
            IllegalArgumentException("Workspace not found")
        }
        
        val tokenExpiresAt = LocalDateTime.now().plusSeconds(expiresIn)
        
        val integration = DiscordIntegration(
            workspace = workspace,
            discordServerId = discordServerId,
            discordServerName = discordServerName,
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenExpiresAt = tokenExpiresAt,
            createdBy = userId
        )
        
        return discordIntegrationRepository.save(integration)
    }
    
    @Transactional
    fun addChannelMapping(
        integrationId: String,
        discordChannelId: String,
        discordChannelName: String,
        gaiaChannelId: String,
        syncDirection: SyncDirection
    ): DiscordChannelMapping? {
        val integration = discordIntegrationRepository.findById(integrationId).orElse(null) ?: return null
        val gaiaChannel = channelRepository.findById(gaiaChannelId).orElse(null) ?: return null
        
        val mapping = DiscordChannelMapping(
            discordIntegration = integration,
            discordChannelId = discordChannelId,
            discordChannelName = discordChannelName,
            gaiaChannel = gaiaChannel,
            syncDirection = syncDirection
        )
        
        integration.channelMappings.add(mapping)
        discordIntegrationRepository.save(integration)
        
        return mapping
    }
    
    // Temporarily returning empty list until Discord4J integration is fixed
    fun getGuildChannels(integrationId: String): kotlin.collections.List<Any> {
        return emptyList()
    }
    
    fun sendMessageToDiscord(integrationId: String, mappingId: String, content: String, sender: String): Boolean {
        // Discord integration temporarily disabled
        return false
    }
    
    @Transactional
    fun refreshToken(integrationId: String): DiscordIntegration? {
        // Discord integration temporarily disabled
        return null
    }
    
    @Transactional
    fun deactivateIntegration(integrationId: String): Boolean {
        val integration = discordIntegrationRepository.findById(integrationId).orElse(null) ?: return false
        
        val updatedIntegration = integration.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )
        
        discordIntegrationRepository.save(updatedIntegration)
        return true
    }
    
    fun getIntegrationsByWorkspace(workspaceId: String): kotlin.collections.List<DiscordIntegration> {
        return discordIntegrationRepository.findByWorkspaceIdAndIsActiveTrue(workspaceId)
    }
}