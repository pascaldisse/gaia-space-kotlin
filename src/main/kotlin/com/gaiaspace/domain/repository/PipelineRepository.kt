package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.Pipeline
import com.gaiaspace.domain.model.PipelineStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import kotlin.collections.List

@Repository
interface PipelineRepository : JpaRepository<Pipeline, String> {
    fun findByRepositoryId(repositoryId: String): List<Pipeline>
    
    fun findByRepositoryIdAndStatus(repositoryId: String, status: PipelineStatus): List<Pipeline>
    
    @Query("""
        SELECT p FROM Pipeline p 
        WHERE p.repository.id = :repositoryId 
        AND LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchByNameInRepository(
        @Param("repositoryId") repositoryId: String, 
        @Param("searchTerm") searchTerm: String
    ): List<Pipeline>
    
    @Query("""
        SELECT p FROM Pipeline p 
        JOIN p.repository r 
        JOIN r.workspace w 
        JOIN w.members m 
        WHERE m.user.id = :userId
    """)
    fun findPipelinesForUser(@Param("userId") userId: String): List<Pipeline>
}