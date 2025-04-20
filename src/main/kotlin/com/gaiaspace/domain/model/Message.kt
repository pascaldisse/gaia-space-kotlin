package com.gaiaspace.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    
    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    val channel: Channel,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: User,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    val replyTo: Message? = null,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,
    
    @Column(name = "is_edited")
    val isEdited: Boolean = false,
    
    @ElementCollection
    @CollectionTable(
        name = "message_attachments",
        joinColumns = [JoinColumn(name = "message_id")]
    )
    @Column(name = "url")
    val attachments: MutableList<String> = mutableListOf(),
    
    @ElementCollection
    @CollectionTable(
        name = "message_reactions",
        joinColumns = [JoinColumn(name = "message_id")]
    )
    val reactions: MutableList<MessageReaction> = mutableListOf()
)

@Embeddable
data class MessageReaction(
    val emoji: String,
    
    @Column(name = "user_id")
    val userId: String
)