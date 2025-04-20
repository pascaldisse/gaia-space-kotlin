package com.gaiaspace.domain.repository

import com.gaiaspace.domain.model.Task
import com.gaiaspace.domain.model.TaskPriority
import com.gaiaspace.domain.model.TaskStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.List

@Repository
interface TaskRepository : JpaRepository<Task, String> {
    fun findByProjectId(projectId: String): List<Task>
    
    fun findByProjectIdAndStatus(projectId: String, status: TaskStatus): List<Task>
    
    fun findByAssigneeId(userId: String): List<Task>
    
    fun findByAssigneeIdAndStatus(userId: String, status: TaskStatus): List<Task>
    
    fun findByDueDateBetween(startDate: LocalDate, endDate: LocalDate): List<Task>
    
    fun findByProjectIdAndPriorityIn(projectId: String, priorities: List<TaskPriority>): List<Task>
    
    @Query("""
        SELECT t FROM Task t 
        WHERE t.project.id = :projectId 
        AND LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchByTitleInProject(
        @Param("projectId") projectId: String, 
        @Param("searchTerm") searchTerm: String
    ): List<Task>
    
    @Query("""
        SELECT t FROM Task t
        JOIN t.tags tag
        WHERE tag.id = :tagId
    """)
    fun findByTagId(@Param("tagId") tagId: String): List<Task>
}