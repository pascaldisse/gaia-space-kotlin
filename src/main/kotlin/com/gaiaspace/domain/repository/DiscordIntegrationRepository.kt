package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.DiscordIntegration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DiscordIntegrationRepository : JpaRepository<DiscordIntegration, String> {
    fun findByWorkspaceId(workspaceId: String): List<DiscordIntegration>
    
    fun findByDiscordServerId(discordServerId: String): Optional<DiscordIntegration>
    
    fun findByWorkspaceIdAndIsActiveTrue(workspaceId: String): List<DiscordIntegration>
}