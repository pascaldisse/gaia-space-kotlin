package com.gaiaspace.service

import com.gaiaspace.domain.model.*
import com.gaiaspace.domain.repository.GitRepositoryRepository
import com.gaiaspace.domain.repository.PipelineRepository
import com.gaiaspace.domain.repository.PipelineRunRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PipelineService(
    private val pipelineRepository: PipelineRepository,
    private val pipelineRunRepository: PipelineRunRepository,
    private val gitRepositoryRepository: GitRepositoryRepository
) {
    fun findById(id: String): Pipeline? {
        return pipelineRepository.findById(id).orElse(null)
    }
    
    fun findByRepository(repositoryId: String): kotlin.collections.List<Pipeline> {
        return pipelineRepository.findByRepositoryId(repositoryId)
    }
    
    fun findPipelineRunById(id: String): PipelineRun? {
        return pipelineRunRepository.findById(id).orElse(null)
    }
    
    fun findRunsByPipeline(pipelineId: String, pageable: Pageable): Page<PipelineRun> {
        return pipelineRunRepository.findByPipelineId(pipelineId, pageable)
    }
    
    @Transactional
    fun createPipeline(
        name: String,
        description: String?,
        repositoryId: String,
        branchPattern: String?,
        createdBy: String
    ): Pipeline {
        val repository = gitRepositoryRepository.findById(repositoryId).orElseThrow {
            IllegalArgumentException("Repository not found")
        }
        
        val pipeline = Pipeline(
            name = name,
            description = description,
            repository = repository,
            branchPattern = branchPattern,
            createdBy = createdBy
        )
        
        return pipelineRepository.save(pipeline)
    }
    
    @Transactional
    fun updatePipeline(
        id: String,
        name: String?,
        description: String?,
        branchPattern: String?,
        status: PipelineStatus?
    ): Pipeline? {
        val pipeline = pipelineRepository.findById(id).orElse(null) ?: return null
        
        val updatedPipeline = pipeline.copy(
            name = name ?: pipeline.name,
            description = description ?: pipeline.description,
            branchPattern = branchPattern ?: pipeline.branchPattern,
            status = status ?: pipeline.status,
            updatedAt = LocalDateTime.now()
        )
        
        return pipelineRepository.save(updatedPipeline)
    }
    
    @Transactional
    fun addJobToPipeline(
        pipelineId: String,
        name: String,
        description: String?,
        position: Int,
        timeoutMinutes: Int?,
        runCondition: String?
    ): PipelineJob? {
        val pipeline = pipelineRepository.findById(pipelineId).orElse(null) ?: return null
        
        val job = PipelineJob(
            name = name,
            description = description,
            pipeline = pipeline,
            position = position,
            timeoutMinutes = timeoutMinutes,
            runCondition = runCondition
        )
        
        pipeline.pipelineJobs.add(job)
        pipelineRepository.save(pipeline)
        
        return job
    }
    
    @Transactional
    fun addStepToJob(
        jobId: String,
        name: String,
        position: Int,
        type: StepType,
        command: String,
        runIfPreviousFailed: Boolean,
        timeoutMinutes: Int?
    ): JobStep? {
        // In a real implementation, this would need a JobRepository
        val pipeline = pipelineRepository.findAll()
            .firstOrNull { pipeline -> pipeline.pipelineJobs.any { it.id == jobId } }
            ?: return null
        
        val job = pipeline.pipelineJobs.firstOrNull { it.id == jobId } ?: return null
        
        val step = JobStep(
            name = name,
            job = job,
            position = position,
            type = type,
            command = command,
            runIfPreviousFailed = runIfPreviousFailed,
            timeoutMinutes = timeoutMinutes
        )
        
        job.steps.add(step)
        pipelineRepository.save(pipeline)
        
        return step
    }
    
    @Transactional
    fun createPipelineRun(
        pipelineId: String,
        commitId: String,
        branchName: String,
        createdBy: String
    ): PipelineRun {
        val pipeline = pipelineRepository.findById(pipelineId).orElseThrow {
            IllegalArgumentException("Pipeline not found")
        }
        
        if (pipeline.status != PipelineStatus.ACTIVE) {
            throw IllegalStateException("Pipeline is not active")
        }
        
        val pipelineRun = PipelineRun(
            pipeline = pipeline,
            commitId = commitId,
            branchName = branchName,
            createdBy = createdBy
        )
        
        // Create job runs for each job in the pipeline
        pipeline.pipelineJobs.forEach { job ->
            val jobRun = JobRun(
                pipelineRun = pipelineRun,
                job = job
            )
            
            // Create step runs for each step in the job
            job.steps.forEach { step ->
                val stepRun = StepRun(
                    jobRun = jobRun,
                    step = step
                )
                
                jobRun.stepRuns.add(stepRun)
            }
            
            pipelineRun.jobRuns.add(jobRun)
        }
        
        pipeline.runs.add(pipelineRun)
        pipelineRepository.save(pipeline)
        
        return pipelineRun
    }
    
    @Transactional
    fun updatePipelineRunStatus(
        runId: String,
        status: RunStatus,
        startedAt: LocalDateTime? = null,
        finishedAt: LocalDateTime? = null
    ): PipelineRun? {
        val pipelineRun = pipelineRunRepository.findById(runId).orElse(null) ?: return null
        
        val updatedRun = pipelineRun.copy(
            status = status,
            startedAt = startedAt ?: pipelineRun.startedAt,
            finishedAt = finishedAt ?: pipelineRun.finishedAt
        )
        
        return pipelineRunRepository.save(updatedRun)
    }
    
    @Transactional
    fun updateJobRunStatus(
        runId: String,
        jobRunId: String,
        status: RunStatus,
        startedAt: LocalDateTime? = null,
        finishedAt: LocalDateTime? = null
    ): JobRun? {
        val pipelineRun = pipelineRunRepository.findById(runId).orElse(null) ?: return null
        
        val jobRunIndex = pipelineRun.jobRuns.indexOfFirst { it.id == jobRunId }
        if (jobRunIndex == -1) return null
        
        val jobRun = pipelineRun.jobRuns[jobRunIndex]
        val updatedJobRun = jobRun.copy(
            status = status,
            startedAt = startedAt ?: jobRun.startedAt,
            finishedAt = finishedAt ?: jobRun.finishedAt
        )
        
        pipelineRun.jobRuns[jobRunIndex] = updatedJobRun
        pipelineRunRepository.save(pipelineRun)
        
        return updatedJobRun
    }
    
    @Transactional
    fun deletePipeline(id: String): Boolean {
        if (!pipelineRepository.existsById(id)) return false
        
        pipelineRepository.deleteById(id)
        return true
    }
}