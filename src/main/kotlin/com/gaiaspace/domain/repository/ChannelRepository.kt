package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.Channel
import com.gaiaspace.domain.model.ChannelType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.List

@Repository
interface ChannelRepository : JpaRepository<Channel, String> {
    fun findByWorkspaceId(workspaceId: String): List<Channel>
    
    fun findByWorkspaceIdAndType(workspaceId: String, type: ChannelType): List<Channel>
    
    @Query("""
        SELECT c FROM Channel c 
        WHERE c.workspace.id = :workspaceId 
        AND LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchByNameInWorkspace(
        @Param("workspaceId") workspaceId: String, 
        @Param("searchTerm") searchTerm: String
    ): List<Channel>
}