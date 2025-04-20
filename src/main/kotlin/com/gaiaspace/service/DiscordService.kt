package com.gaiaspace.service

import com.gaiaspace.domain.model.*
import com.gaiaspace.domain.repository.ChannelRepository
import com.gaiaspace.domain.repository.DiscordIntegrationRepository
import com.gaiaspace.domain.repository.WorkspaceRepository
import discord4j.common.util.Snowflake
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.object.entity.Guild
import discord4j.core.object.entity.channel.TextChannel
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.RestClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class DiscordService(
    private val discordIntegrationRepository: DiscordIntegrationRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val channelRepository: ChannelRepository,
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String
) {
    private fun createDiscordClient(token: String): DiscordClient {
        return DiscordClient.create(token)
    }
    
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
    
    fun getGuildChannels(integrationId: String): List<discord4j.core.object.entity.channel.Channel> {
        val integration = discordIntegrationRepository.findById(integrationId).orElseThrow {
            IllegalArgumentException("Discord integration not found")
        }
        
        val client = createDiscordClient(integration.accessToken)
        val restClient = client.rest()
        
        val guildId = Snowflake.of(integration.discordServerId)
        
        return restClient.guildService
            .getGuildChannels(guildId)
            .collectList()
            .block() ?: emptyList()
    }
    
    fun sendMessageToDiscord(integrationId: String, mappingId: String, content: String, sender: String): Boolean {
        val integration = discordIntegrationRepository.findById(integrationId).orElse(null) ?: return false
        
        val mapping = integration.channelMappings.find { it.id == mappingId } ?: return false
        
        if (mapping.syncDirection == SyncDirection.GAIA_TO_DISCORD || 
            mapping.syncDirection == SyncDirection.BIDIRECTIONAL) {
            
            val client = createDiscordClient(integration.accessToken)
            val restClient = client.rest()
            
            val channelId = Snowflake.of(mapping.discordChannelId)
            
            try {
                val formattedMessage = "**$sender:** $content"
                
                restClient.channelService.createMessage(
                    channelId,
                    MessageCreateSpec.builder()
                        .content(formattedMessage)
                        .build().asRequest()
                ).block()
                
                return true
            } catch (e: Exception) {
                // Handle token expiration and other errors
                return false
            }
        }
        
        return false
    }
    
    @Transactional
    fun refreshToken(integrationId: String): DiscordIntegration? {
        val integration = discordIntegrationRepository.findById(integrationId).orElse(null) ?: return null
        
        try {
            val client = createDiscordClient("")
            val oauthClient = client.oauth2Service
            
            val tokenResponse = oauthClient.refreshAccessToken(clientId, clientSecret, integration.refreshToken).block()
                ?: return null
            
            val updatedIntegration = integration.copy(
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken ?: integration.refreshToken,
                tokenExpiresAt = LocalDateTime.now().plusSeconds(tokenResponse.expiresIn.toLong()),
                updatedAt = LocalDateTime.now()
            )
            
            return discordIntegrationRepository.save(updatedIntegration)
        } catch (e: Exception) {
            return null
        }
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
}