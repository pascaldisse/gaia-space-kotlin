package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "workspaces")
data class Workspace(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    val description: String? = null,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(name = "avatar_url")
    val avatarUrl: String? = null,
    
    @OneToMany(mappedBy = "workspace", cascade = [CascadeType.ALL], orphanRemoval = true)
    val channels: MutableList<Channel> = mutableListOf(),
    
    @OneToMany(mappedBy = "workspace", cascade = [CascadeType.ALL], orphanRemoval = true)
    val projects: MutableList<Project> = mutableListOf(),
    
    @OneToMany(mappedBy = "workspace", cascade = [CascadeType.ALL], orphanRemoval = true)
    val members: MutableList<WorkspaceMember> = mutableListOf()
)