package com.gaiaspace.controller

import com.gaiaspace.domain.model.SyncDirection
import com.gaiaspace.dto.request.AddChannelMappingRequest
import com.gaiaspace.dto.request.CreateDiscordIntegrationRequest
import com.gaiaspace.dto.request.SendDiscordMessageRequest
import com.gaiaspace.dto.response.DiscordChannelMappingResponse
import com.gaiaspace.dto.response.DiscordChannelResponse
import com.gaiaspace.dto.response.DiscordIntegrationResponse
import com.gaiaspace.service.DiscordService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/discord")
class DiscordController(
    private val discordService: DiscordService
) {
    @PostMapping("/integrations")
    @PreAuthorize("isAuthenticated()")
    fun createIntegration(
        @RequestAttribute("userId") userId: String,
        @Valid @RequestBody request: CreateDiscordIntegrationRequest
    ): ResponseEntity<DiscordIntegrationResponse> {
        val integration = discordService.createIntegration(
            workspaceId = request.workspaceId,
            discordServerId = request.discordServerId,
            discordServerName = request.discordServerName,
            accessToken = request.accessToken,
            refreshToken = request.refreshToken,
            expiresIn = request.expiresIn,
            userId = userId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(DiscordIntegrationResponse.fromDiscordIntegration(integration))
    }
    
    @GetMapping("/integrations/workspace/{workspaceId}")
    @PreAuthorize("isAuthenticated()")
    fun getIntegrationsByWorkspace(@PathVariable workspaceId: String): ResponseEntity<List<DiscordIntegrationResponse>> {
        val integrations = discordService.getIntegrationsByWorkspace(workspaceId)
        
        return ResponseEntity.ok(
            integrations.map { DiscordIntegrationResponse.fromDiscordIntegration(it) }
        )
    }
    
    @GetMapping("/integrations/{id}/channels")
    @PreAuthorize("isAuthenticated()")
    fun getGuildChannels(@PathVariable id: String): ResponseEntity<List<DiscordChannelResponse>> {
        try {
            // Temporarily returning empty list until Discord integration is fixed
            val channels = emptyList<DiscordChannelResponse>()
            
            return ResponseEntity.ok(channels)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }
    
    @PostMapping("/integrations/{id}/channel-mappings")
    @PreAuthorize("isAuthenticated()")
    fun addChannelMapping(
        @PathVariable id: String,
        @Valid @RequestBody request: AddChannelMappingRequest
    ): ResponseEntity<DiscordChannelMappingResponse> {
        val mapping = discordService.addChannelMapping(
            integrationId = id,
            discordChannelId = request.discordChannelId,
            discordChannelName = request.discordChannelName,
            gaiaChannelId = request.gaiaChannelId,
            syncDirection = SyncDirection.valueOf(request.syncDirection)
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(DiscordChannelMappingResponse.fromDiscordChannelMapping(mapping))
    }
    
    @PostMapping("/integrations/{id}/refresh-token")
    @PreAuthorize("isAuthenticated()")
    fun refreshToken(@PathVariable id: String): ResponseEntity<DiscordIntegrationResponse> {
        val integration = discordService.refreshToken(id)
            ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        
        return ResponseEntity.ok(DiscordIntegrationResponse.fromDiscordIntegration(integration))
    }
    
    @PostMapping("/integrations/{id}/channels/{mappingId}/messages")
    @PreAuthorize("isAuthenticated()")
    fun sendMessage(
        @PathVariable id: String,
        @PathVariable mappingId: String,
        @Valid @RequestBody request: SendDiscordMessageRequest
    ): ResponseEntity<Void> {
        val success = discordService.sendMessageToDiscord(
            integrationId = id,
            mappingId = mappingId,
            content = request.content,
            sender = request.sender
        )
        
        return if (success) ResponseEntity.ok().build()
        else ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    }
    
    @PutMapping("/integrations/{id}/deactivate")
    @PreAuthorize("isAuthenticated()")
    fun deactivateIntegration(@PathVariable id: String): ResponseEntity<Void> {
        val success = discordService.deactivateIntegration(id)
        
        return if (success) ResponseEntity.ok().build()
        else ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
}