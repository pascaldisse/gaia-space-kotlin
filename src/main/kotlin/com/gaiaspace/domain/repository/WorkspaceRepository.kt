package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.Workspace
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import kotlin.collections.List

@Repository
interface WorkspaceRepository : JpaRepository<Workspace, String> {
    fun findByCreatedBy(userId: String): List<Workspace>
    
    @Query("""
        SELECT w FROM Workspace w 
        JOIN w.members m 
        WHERE m.user.id = :userId
    """)
    fun findByMemberId(@Param("userId") userId: String): List<Workspace>
    
    @Query("""
        SELECT w FROM Workspace w 
        JOIN w.members m 
        WHERE m.user.id = :userId 
        AND LOWER(w.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchByNameForUser(
        @Param("userId") userId: String, 
        @Param("searchTerm") searchTerm: String
    ): List<Workspace>
}