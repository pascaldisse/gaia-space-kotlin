package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val title: String,
    
    @Column(columnDefinition = "TEXT")
    val content: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_document_id")
    val parentDocument: Document? = null,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @OneToMany(mappedBy = "document", cascade = [CascadeType.ALL], orphanRemoval = true)
    val versions: MutableList<DocumentVersion> = mutableListOf(),
    
    @OneToMany(mappedBy = "parentDocument", cascade = [CascadeType.ALL], orphanRemoval = true)
    val children: MutableList<Document> = mutableListOf()
)

@Entity
@Table(name = "document_versions")
data class DocumentVersion(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val version: Int,
    
    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    val document: Document,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "comment")
    val comment: String? = null
)