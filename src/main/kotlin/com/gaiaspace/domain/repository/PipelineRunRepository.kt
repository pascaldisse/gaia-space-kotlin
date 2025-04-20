package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.PipelineRun
import com.gaiaspace.domain.model.RunStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.List

@Repository
interface PipelineRunRepository : JpaRepository<PipelineRun, String> {
    fun findByPipelineId(pipelineId: String, pageable: Pageable): Page<PipelineRun>
    
    fun findByPipelineIdAndStatus(pipelineId: String, status: RunStatus): List<PipelineRun>
    
    fun findByBranchName(branchName: String, pageable: Pageable): Page<PipelineRun>
    
    @Query("""
        SELECT r FROM PipelineRun r 
        JOIN r.pipeline p 
        JOIN p.repository repo 
        WHERE repo.id = :repositoryId 
        AND r.branchName = :branchName
    """)
    fun findByRepositoryAndBranch(
        @Param("repositoryId") repositoryId: String,
        @Param("branchName") branchName: String,
        pageable: Pageable
    ): Page<PipelineRun>
    
    @Query("""
        SELECT r FROM PipelineRun r 
        JOIN r.pipeline p 
        JOIN p.repository repo 
        JOIN repo.workspace w 
        JOIN w.members m 
        WHERE m.user.id = :userId
    """)
    fun findRunsForUser(@Param("userId") userId: String, pageable: Pageable): Page<PipelineRun>
}