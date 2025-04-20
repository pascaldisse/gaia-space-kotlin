package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.Project
import com.gaiaspace.domain.model.ProjectStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.List

@Repository
interface ProjectRepository : JpaRepository<Project, String> {
    fun findByWorkspaceId(workspaceId: String): List<Project>
    
    fun findByWorkspaceIdAndStatus(workspaceId: String, status: ProjectStatus): List<Project>
    
    @Query("""
        SELECT p FROM Project p 
        WHERE p.workspace.id = :workspaceId 
        AND LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchByNameInWorkspace(
        @Param("workspaceId") workspaceId: String, 
        @Param("searchTerm") searchTerm: String
    ): List<Project>
    
    @Query("""
        SELECT p FROM Project p 
        JOIN p.workspace w 
        JOIN w.members m 
        WHERE m.user.id = :userId
    """)
    fun findProjectsForUser(@Param("userId") userId: String): List<Project>
}