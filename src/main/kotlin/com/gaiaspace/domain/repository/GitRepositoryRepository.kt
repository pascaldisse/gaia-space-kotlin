package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.GitRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.List

@Repository
interface GitRepositoryRepository : JpaRepository<GitRepository, String> {
    fun findByWorkspaceId(workspaceId: String): List<GitRepository>
    
    fun findByProjectId(projectId: String): List<GitRepository>
    
    @Query("""
        SELECT r FROM GitRepository r 
        WHERE r.workspace.id = :workspaceId 
        AND LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchByNameInWorkspace(
        @Param("workspaceId") workspaceId: String, 
        @Param("searchTerm") searchTerm: String
    ): List<GitRepository>
}