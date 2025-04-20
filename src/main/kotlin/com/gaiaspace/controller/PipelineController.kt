package com.gaiaspace.controller

import com.gaiaspace.domain.model.RunStatus
import com.gaiaspace.domain.model.StepType
import com.gaiaspace.dto.request.*
import com.gaiaspace.dto.response.*
import com.gaiaspace.service.PipelineService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/pipelines")
class PipelineController(
    private val pipelineService: PipelineService
) {
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getPipelineById(@PathVariable id: String): ResponseEntity<PipelineResponse> {
        val pipeline = pipelineService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(PipelineResponse.fromPipeline(pipeline))
    }
    
    @GetMapping("/repository/{repositoryId}")
    @PreAuthorize("isAuthenticated()")
    fun getPipelinesByRepository(@PathVariable repositoryId: String): ResponseEntity<List<PipelineResponse>> {
        val pipelines = pipelineService.findByRepository(repositoryId)
        
        return ResponseEntity.ok(pipelines.map { PipelineResponse.fromPipeline(it) })
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createPipeline(
        @RequestAttribute("userId") userId: String,
        @Valid @RequestBody request: CreatePipelineRequest
    ): ResponseEntity<PipelineResponse> {
        val pipeline = pipelineService.createPipeline(
            name = request.name,
            description = request.description,
            repositoryId = request.repositoryId,
            branchPattern = request.branchPattern,
            createdBy = userId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PipelineResponse.fromPipeline(pipeline))
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun updatePipeline(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdatePipelineRequest
    ): ResponseEntity<PipelineResponse> {
        val pipeline = pipelineService.updatePipeline(
            id = id,
            name = request.name,
            description = request.description,
            branchPattern = request.branchPattern,
            status = request.status
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(PipelineResponse.fromPipeline(pipeline))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun deletePipeline(@PathVariable id: String): ResponseEntity<Void> {
        val success = pipelineService.deletePipeline(id)
        
        return if (success) ResponseEntity.noContent().build()
        else ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
    
    @PostMapping("/{id}/jobs")
    @PreAuthorize("isAuthenticated()")
    fun addJob(
        @PathVariable id: String,
        @Valid @RequestBody request: AddJobRequest
    ): ResponseEntity<PipelineJobResponse> {
        val job = pipelineService.addJobToPipeline(
            pipelineId = id,
            name = request.name,
            description = request.description,
            position = request.position,
            timeoutMinutes = request.timeoutMinutes,
            runCondition = request.runCondition
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PipelineJobResponse.fromPipelineJob(job))
    }
    
    @PostMapping("/jobs/{jobId}/steps")
    @PreAuthorize("isAuthenticated()")
    fun addStep(
        @PathVariable jobId: String,
        @Valid @RequestBody request: AddStepRequest
    ): ResponseEntity<JobStepResponse> {
        val step = pipelineService.addStepToJob(
            jobId = jobId,
            name = request.name,
            position = request.position,
            type = StepType.valueOf(request.type),
            command = request.command,
            runIfPreviousFailed = request.runIfPreviousFailed,
            timeoutMinutes = request.timeoutMinutes
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(JobStepResponse.fromJobStep(step))
    }
    
    @PostMapping("/{id}/runs")
    @PreAuthorize("isAuthenticated()")
    fun createRun(
        @RequestAttribute("userId") userId: String,
        @PathVariable id: String,
        @Valid @RequestBody request: CreatePipelineRunRequest
    ): ResponseEntity<PipelineRunResponse> {
        val run = pipelineService.createPipelineRun(
            pipelineId = id,
            commitId = request.commitId,
            branchName = request.branchName,
            createdBy = userId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PipelineRunResponse.fromPipelineRun(run))
    }
    
    @GetMapping("/{id}/runs")
    @PreAuthorize("isAuthenticated()")
    fun getPipelineRuns(
        @PathVariable id: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PipelineRunResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val runs = pipelineService.findRunsByPipeline(id, pageable)
        
        return ResponseEntity.ok(runs.map { PipelineRunResponse.fromPipelineRun(it) })
    }
    
    @GetMapping("/runs/{runId}")
    @PreAuthorize("isAuthenticated()")
    fun getPipelineRunById(@PathVariable runId: String): ResponseEntity<PipelineRunResponse> {
        val run = pipelineService.findPipelineRunById(runId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(PipelineRunResponse.fromPipelineRun(run))
    }
    
    @PutMapping("/runs/{runId}/status")
    @PreAuthorize("isAuthenticated()")
    fun updateRunStatus(
        @PathVariable runId: String,
        @Valid @RequestBody request: UpdateRunStatusRequest
    ): ResponseEntity<PipelineRunResponse> {
        val run = pipelineService.updatePipelineRunStatus(
            runId = runId,
            status = RunStatus.valueOf(request.status),
            startedAt = request.startedAt,
            finishedAt = request.finishedAt
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(PipelineRunResponse.fromPipelineRun(run))
    }
    
    @PutMapping("/runs/{runId}/jobs/{jobRunId}/status")
    @PreAuthorize("isAuthenticated()")
    fun updateJobRunStatus(
        @PathVariable runId: String,
        @PathVariable jobRunId: String,
        @Valid @RequestBody request: UpdateRunStatusRequest
    ): ResponseEntity<JobRunResponse> {
        val jobRun = pipelineService.updateJobRunStatus(
            runId = runId,
            jobRunId = jobRunId,
            status = RunStatus.valueOf(request.status),
            startedAt = request.startedAt,
            finishedAt = request.finishedAt
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(JobRunResponse.fromJobRun(jobRun))
    }
}

// These would typically be defined in separate files in the dto.request and dto.response packages
data class CreatePipelineRequest(
    val name: String,
    val description: String? = null,
    val repositoryId: String,
    val branchPattern: String? = null
)

data class UpdatePipelineRequest(
    val name: String? = null,
    val description: String? = null,
    val branchPattern: String? = null,
    val status: com.gaiaspace.domain.model.PipelineStatus? = null
)

data class AddJobRequest(
    val name: String,
    val description: String? = null,
    val position: Int,
    val timeoutMinutes: Int? = null,
    val runCondition: String? = null
)

data class AddStepRequest(
    val name: String,
    val position: Int,
    val type: String,
    val command: String,
    val runIfPreviousFailed: Boolean = false,
    val timeoutMinutes: Int? = null
)

data class CreatePipelineRunRequest(
    val commitId: String,
    val branchName: String
)

data class UpdateRunStatusRequest(
    val status: String,
    val startedAt: LocalDateTime? = null,
    val finishedAt: LocalDateTime? = null
)

class PipelineResponse private constructor(
    val id: String,
    val name: String,
    val description: String?,
    val repositoryId: String,
    val repositoryName: String,
    val branchPattern: String?,
    val status: String,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val jobs: List<PipelineJobResponse>
) {
    companion object {
        fun fromPipeline(pipeline: com.gaiaspace.domain.model.Pipeline): PipelineResponse {
            return PipelineResponse(
                id = pipeline.id,
                name = pipeline.name,
                description = pipeline.description,
                repositoryId = pipeline.repository.id,
                repositoryName = pipeline.repository.name,
                branchPattern = pipeline.branchPattern,
                status = pipeline.status.name,
                createdBy = pipeline.createdBy,
                createdAt = pipeline.createdAt,
                updatedAt = pipeline.updatedAt,
                jobs = pipeline.pipelineJobs.map { PipelineJobResponse.fromPipelineJob(it) }
            )
        }
    }
}

class PipelineJobResponse private constructor(
    val id: String,
    val name: String,
    val description: String?,
    val position: Int,
    val timeoutMinutes: Int?,
    val runCondition: String?,
    val steps: List<JobStepResponse>
) {
    companion object {
        fun fromPipelineJob(job: com.gaiaspace.domain.model.PipelineJob): PipelineJobResponse {
            return PipelineJobResponse(
                id = job.id,
                name = job.name,
                description = job.description,
                position = job.position,
                timeoutMinutes = job.timeoutMinutes,
                runCondition = job.runCondition,
                steps = job.steps.map { JobStepResponse.fromJobStep(it) }
            )
        }
    }
}

class JobStepResponse private constructor(
    val id: String,
    val name: String,
    val position: Int,
    val type: String,
    val command: String,
    val runIfPreviousFailed: Boolean,
    val timeoutMinutes: Int?
) {
    companion object {
        fun fromJobStep(step: com.gaiaspace.domain.model.JobStep): JobStepResponse {
            return JobStepResponse(
                id = step.id,
                name = step.name,
                position = step.position,
                type = step.type.name,
                command = step.command,
                runIfPreviousFailed = step.runIfPreviousFailed,
                timeoutMinutes = step.timeoutMinutes
            )
        }
    }
}

class PipelineRunResponse private constructor(
    val id: String,
    val pipelineId: String,
    val pipelineName: String,
    val commitId: String,
    val branchName: String,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val status: String,
    val jobRuns: List<JobRunResponse>
) {
    companion object {
        fun fromPipelineRun(run: com.gaiaspace.domain.model.PipelineRun): PipelineRunResponse {
            return PipelineRunResponse(
                id = run.id,
                pipelineId = run.pipeline.id,
                pipelineName = run.pipeline.name,
                commitId = run.commitId,
                branchName = run.branchName,
                createdBy = run.createdBy,
                createdAt = run.createdAt,
                startedAt = run.startedAt,
                finishedAt = run.finishedAt,
                status = run.status.name,
                jobRuns = run.jobRuns.map { JobRunResponse.fromJobRun(it) }
            )
        }
    }
}

class JobRunResponse private constructor(
    val id: String,
    val jobId: String,
    val jobName: String,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val status: String,
    val stepRuns: List<StepRunResponse>
) {
    companion object {
        fun fromJobRun(jobRun: com.gaiaspace.domain.model.JobRun): JobRunResponse {
            return JobRunResponse(
                id = jobRun.id,
                jobId = jobRun.job.id,
                jobName = jobRun.job.name,
                startedAt = jobRun.startedAt,
                finishedAt = jobRun.finishedAt,
                status = jobRun.status.name,
                stepRuns = jobRun.stepRuns.map { StepRunResponse.fromStepRun(it) }
            )
        }
    }
}

class StepRunResponse private constructor(
    val id: String,
    val stepId: String,
    val stepName: String,
    val type: String,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val status: String,
    val exitCode: Int?,
    val output: String?
) {
    companion object {
        fun fromStepRun(stepRun: com.gaiaspace.domain.model.StepRun): StepRunResponse {
            return StepRunResponse(
                id = stepRun.id,
                stepId = stepRun.step.id,
                stepName = stepRun.step.name,
                type = stepRun.step.type.name,
                startedAt = stepRun.startedAt,
                finishedAt = stepRun.finishedAt,
                status = stepRun.status.name,
                exitCode = stepRun.exitCode,
                output = stepRun.output
            )
        }
    }
}