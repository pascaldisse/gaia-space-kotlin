package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.List

@Repository
interface MessageRepository : JpaRepository<Message, String> {
    fun findByChannelId(channelId: String, pageable: Pageable): Page<Message>
    
    fun findByChannelIdAndCreatedAtAfter(channelId: String, timestamp: LocalDateTime): List<Message>
    
    fun findBySenderId(userId: String, pageable: Pageable): Page<Message>
    
    @Query("""
        SELECT m FROM Message m 
        WHERE m.channel.id = :channelId 
        AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchByContentInChannel(
        @Param("channelId") channelId: String, 
        @Param("searchTerm") searchTerm: String,
        pageable: Pageable
    ): Page<Message>
}