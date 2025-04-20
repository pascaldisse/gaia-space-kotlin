package com.gaiaspace.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateDiscordIntegrationRequest(
    @field:NotBlank(message = "Workspace ID is required")
    val workspaceId: String,
    
    @field:NotBlank(message = "Discord server ID is required")
    val discordServerId: String,
    
    @field:NotBlank(message = "Discord server name is required")
    val discordServerName: String,
    
    @field:NotBlank(message = "Access token is required")
    val accessToken: String,
    
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String,
    
    @field:Positive(message = "Expires in must be a positive number")
    val expiresIn: Long
)

data class AddChannelMappingRequest(
    @field:NotBlank(message = "Discord channel ID is required")
    val discordChannelId: String,
    
    @field:NotBlank(message = "Discord channel name is required")
    val discordChannelName: String,
    
    @field:NotBlank(message = "Gaia channel ID is required")
    val gaiaChannelId: String,
    
    @field:NotBlank(message = "Sync direction is required")
    val syncDirection: String
)

data class SendDiscordMessageRequest(
    @field:NotBlank(message = "Content is required")
    val content: String,
    
    @field:NotBlank(message = "Sender is required")
    val sender: String
)