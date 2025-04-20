package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "channels")
data class Channel(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(nullable = false)
    val name: String,
    
    val description: String? = null,
    
    @Column(name = "channel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ChannelType,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    val workspace: Workspace,
    
    @Column(name = "created_by", nullable = false)
    val createdBy: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @OneToMany(mappedBy = "channel", cascade = [CascadeType.ALL], orphanRemoval = true)
    val messages: MutableList<Message> = mutableListOf()
)

enum class ChannelType {
    TEXT, VOICE, DOCUMENT, TASK, CALENDAR
}