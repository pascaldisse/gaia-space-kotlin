package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "tasks")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val title: String,
    
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    val assignee: User? = null,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: TaskStatus = TaskStatus.TODO,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val priority: TaskPriority = TaskPriority.MEDIUM,
    
    @Column(name = "due_date")
    val dueDate: LocalDate? = null,
    
    @Column(name = "estimated_hours")
    val estimatedHours: Float? = null,
    
    @Column(name = "spent_hours")
    val spentHours: Float? = null,
    
    @OneToMany(mappedBy = "dependentTask", cascade = [CascadeType.ALL], orphanRemoval = true)
    val dependencies: MutableList<TaskDependency> = mutableListOf(),
    
    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<TaskComment> = mutableListOf(),
    
    @ManyToMany
    @JoinTable(
        name = "task_tags",
        joinColumns = [JoinColumn(name = "task_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableSet<Tag> = mutableSetOf()
)

enum class TaskStatus {
    TODO, IN_PROGRESS, REVIEW, DONE, CANCELED
}

enum class TaskPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Entity
@Table(name = "task_dependencies")
data class TaskDependency(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_task_id", nullable = false)
    val dependentTask: Task,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_task_id", nullable = false)
    val prerequisiteTask: Task,
    
    @Column(name = "dependency_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: DependencyType = DependencyType.BLOCKS
)

enum class DependencyType {
    BLOCKS, RELATES_TO, DUPLICATES
}

@Entity
@Table(name = "task_comments")
data class TaskComment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    val task: Task,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(name = "is_edited")
    val isEdited: Boolean = false
)

@Entity
@Table(name = "tags")
data class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false, unique = true)
    val name: String,
    
    @Column(name = "color_hex")
    val colorHex: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    val workspace: Workspace
)