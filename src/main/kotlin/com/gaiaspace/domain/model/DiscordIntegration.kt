package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "discord_integrations")
data class DiscordIntegration(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    val workspace: Workspace,
    
    @Column(name = "discord_server_id", nullable = false)
    val discordServerId: String,
    
    @Column(name = "discord_server_name", nullable = false)
    val discordServerName: String,
    
    @Column(name = "access_token", nullable = false)
    val accessToken: String,
    
    @Column(name = "refresh_token", nullable = false)
    val refreshToken: String,
    
    @Column(name = "token_expires_at", nullable = false)
    val tokenExpiresAt: LocalDateTime,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,
    
    @OneToMany(mappedBy = "discordIntegration", cascade = [CascadeType.ALL], orphanRemoval = true)
    val channelMappings: MutableList<DiscordChannelMapping> = mutableListOf()
)

@Entity
@Table(name = "discord_channel_mappings")
data class DiscordChannelMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discord_integration_id", nullable = false)
    val discordIntegration: DiscordIntegration,
    
    @Column(name = "discord_channel_id", nullable = false)
    val discordChannelId: String,
    
    @Column(name = "discord_channel_name", nullable = false)
    val discordChannelName: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaia_channel_id", nullable = false)
    val gaiaChannel: Channel,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(name = "sync_enabled", nullable = false)
    val syncEnabled: Boolean = true,
    
    @Column(name = "sync_direction", nullable = false)
    @Enumerated(EnumType.STRING)
    val syncDirection: SyncDirection = SyncDirection.BIDIRECTIONAL
)

enum class SyncDirection {
    DISCORD_TO_GAIA, GAIA_TO_DISCORD, BIDIRECTIONAL
}