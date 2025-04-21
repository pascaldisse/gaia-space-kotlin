package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "projects")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    val description: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    val workspace: Workspace,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: ProjectStatus = ProjectStatus.ACTIVE,
    
    @Column(name = "due_date")
    val dueDate: LocalDate? = null,
    
    @Column(name = "avatar_url")
    val avatarUrl: String? = null,
    
    @Column(name = "icon")
    val icon: String = "🚀",
    
    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    val boards: MutableList<Board> = mutableListOf(),
    
    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    val tasks: MutableList<Task> = mutableListOf(),
    
    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    val documents: MutableList<Document> = mutableListOf()
)

enum class ProjectStatus {
    PLANNING, ACTIVE, ON_HOLD, COMPLETED, CANCELED
}