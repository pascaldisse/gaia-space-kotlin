package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "boards")
data class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    val description: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true)
    val columns: MutableList<BoardColumn> = mutableListOf()
)

@Entity
@Table(name = "board_columns")
data class BoardColumn(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    @Column(nullable = false)
    val position: Int,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    val board: Board,
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "board_column_tasks",
        joinColumns = [JoinColumn(name = "column_id")],
        inverseJoinColumns = [JoinColumn(name = "task_id")]
    )
    val tasks: MutableList<Task> = mutableListOf()
)