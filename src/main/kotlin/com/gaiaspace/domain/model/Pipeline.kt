package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "pipelines")
data class Pipeline(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    val description: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    val repository: GitRepository,
    
    @Column(name = "branch_pattern")
    val branchPattern: String? = null,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: PipelineStatus = PipelineStatus.ACTIVE,
    
    @OneToMany(mappedBy = "pipeline", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pipelineJobs: MutableList<PipelineJob> = mutableListOf(),
    
    @OneToMany(mappedBy = "pipeline", cascade = [CascadeType.ALL], orphanRemoval = true)
    val runs: MutableList<PipelineRun> = mutableListOf()
)

enum class PipelineStatus {
    ACTIVE, PAUSED, ARCHIVED
}

@Entity
@Table(name = "pipeline_jobs")
data class PipelineJob(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    val description: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false)
    val pipeline: Pipeline,
    
    @Column(nullable = false)
    val position: Int,
    
    @Column(name = "timeout_minutes")
    val timeoutMinutes: Int? = null,
    
    @Column(name = "run_condition", columnDefinition = "TEXT")
    val runCondition: String? = null,
    
    @OneToMany(mappedBy = "job", cascade = [CascadeType.ALL], orphanRemoval = true)
    val steps: MutableList<JobStep> = mutableListOf()
)

@Entity
@Table(name = "job_steps")
data class JobStep(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    val job: PipelineJob,
    
    @Column(nullable = false)
    val position: Int,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: StepType,
    
    @Column(columnDefinition = "TEXT", nullable = false)
    val command: String,
    
    @Column(name = "run_if_previous_failed")
    val runIfPreviousFailed: Boolean = false,
    
    @Column(name = "timeout_minutes")
    val timeoutMinutes: Int? = null
)

enum class StepType {
    SHELL, DOCKER, BUILD, TEST, DEPLOY
}

@Entity
@Table(name = "pipeline_runs")
data class PipelineRun(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false)
    val pipeline: Pipeline,
    
    @Column(name = "commit_id", nullable = false)
    val commitId: String,
    
    @Column(name = "branch_name", nullable = false)
    val branchName: String,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "started_at")
    val startedAt: LocalDateTime? = null,
    
    @Column(name = "finished_at")
    val finishedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: RunStatus = RunStatus.PENDING,
    
    @OneToMany(mappedBy = "pipelineRun", cascade = [CascadeType.ALL], orphanRemoval = true)
    val jobRuns: MutableList<JobRun> = mutableListOf()
)

enum class RunStatus {
    PENDING, RUNNING, SUCCESS, FAILED, CANCELED, TIMED_OUT
}

@Entity
@Table(name = "job_runs")
data class JobRun(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_run_id", nullable = false)
    val pipelineRun: PipelineRun,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    val job: PipelineJob,
    
    @Column(name = "started_at")
    val startedAt: LocalDateTime? = null,
    
    @Column(name = "finished_at")
    val finishedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: RunStatus = RunStatus.PENDING,
    
    @OneToMany(mappedBy = "jobRun", cascade = [CascadeType.ALL], orphanRemoval = true)
    val stepRuns: MutableList<StepRun> = mutableListOf()
)

@Entity
@Table(name = "step_runs")
data class StepRun(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_run_id", nullable = false)
    val jobRun: JobRun,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    val step: JobStep,
    
    @Column(name = "started_at")
    val startedAt: LocalDateTime? = null,
    
    @Column(name = "finished_at")
    val finishedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: RunStatus = RunStatus.PENDING,
    
    @Column(name = "exit_code")
    val exitCode: Int? = null,
    
    @Column(columnDefinition = "TEXT")
    val output: String? = null
)