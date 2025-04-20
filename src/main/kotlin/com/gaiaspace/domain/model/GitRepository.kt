package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "git_repositories")
data class GitRepository(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    val description: String? = null,
    
    @Column(name = "git_url", nullable = false)
    val gitUrl: String,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: GitProvider,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    val workspace: Workspace,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project? = null,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(name = "default_branch")
    val defaultBranch: String = "main",
    
    @OneToMany(mappedBy = "repository", cascade = [CascadeType.ALL], orphanRemoval = true)
    val branches: MutableList<GitBranch> = mutableListOf(),
    
    @OneToMany(mappedBy = "repository", cascade = [CascadeType.ALL], orphanRemoval = true)
    val mergeRequests: MutableList<MergeRequest> = mutableListOf()
)

enum class GitProvider {
    GITHUB, GITLAB, BITBUCKET, GITEA, CUSTOM
}

@Entity
@Table(name = "git_branches")
data class GitBranch(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    val repository: GitRepository,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "last_commit_id")
    val lastCommitId: String? = null,
    
    @Column(name = "last_commit_message")
    val lastCommitMessage: String? = null,
    
    @Column(name = "last_commit_date")
    val lastCommitDate: LocalDateTime? = null
)

@Entity
@Table(name = "merge_requests")
data class MergeRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val title: String,
    
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    val repository: GitRepository,
    
    @Column(name = "source_branch", nullable = false)
    val sourceBranch: String,
    
    @Column(name = "target_branch", nullable = false)
    val targetBranch: String,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: MergeRequestStatus = MergeRequestStatus.OPEN,
    
    @Column(name = "merged_at")
    val mergedAt: LocalDateTime? = null,
    
    @Column(name = "merged_by")
    val mergedBy: String? = null,
    
    @Column(name = "closed_at")
    val closedAt: LocalDateTime? = null,
    
    @Column(name = "closed_by")
    val closedBy: String? = null,
    
    @OneToMany(mappedBy = "mergeRequest", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<MergeRequestComment> = mutableListOf()
)

enum class MergeRequestStatus {
    OPEN, MERGED, CLOSED
}

@Entity
@Table(name = "merge_request_comments")
data class MergeRequestComment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merge_request_id", nullable = false)
    val mergeRequest: MergeRequest,
    
    @Column(name = "file_path")
    val filePath: String? = null,
    
    @Column(name = "line_number")
    val lineNumber: Int? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null
)