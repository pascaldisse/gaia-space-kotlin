package com.gaiaspace.service

import com.gaiaspace.domain.model.Task
import com.gaiaspace.domain.model.TaskPriority
import com.gaiaspace.domain.model.TaskStatus
import com.gaiaspace.domain.repository.ProjectRepository
import com.gaiaspace.domain.repository.TaskRepository
import com.gaiaspace.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) {
    
    fun findById(id: String): Task? {
        return taskRepository.findById(id).orElse(null)
    }
    
    fun findByProjectId(projectId: String): List<Task> {
        return taskRepository.findByProjectId(projectId).toList()
    }
    
    fun findByProjectIdAndStatus(projectId: String, status: TaskStatus): List<Task> {
        return taskRepository.findByProjectIdAndStatus(projectId, status).toList()
    }
    
    fun findByAssigneeId(assigneeId: String): List<Task> {
        return taskRepository.findByAssigneeId(assigneeId).toList()
    }
    
    fun findByAssigneeIdAndStatus(assigneeId: String, status: TaskStatus): List<Task> {
        return taskRepository.findByAssigneeIdAndStatus(assigneeId, status).toList()
    }
    
    fun findByDueDateRange(start: LocalDate, end: LocalDate): List<Task> {
        return taskRepository.findByDueDateBetween(start, end).toList()
    }
    
    fun findByTagId(tagId: String): List<Task> {
        return taskRepository.findByTagId(tagId).toList()
    }
    
    fun searchTasksByTitle(projectId: String, searchTerm: String): List<Task> {
        return taskRepository.searchByTitleInProject(projectId, searchTerm).toList()
    }
    
    fun countTasksByProjectId(projectId: String): Long {
        return taskRepository.countByProjectId(projectId)
    }
    
    fun countTasksByProjectIdAndStatus(projectId: String, status: TaskStatus): Long {
        return taskRepository.countByProjectIdAndStatus(projectId, status)
    }
    
    @Transactional
    fun createTask(
        title: String,
        description: String?,
        projectId: String,
        assigneeId: String?,
        status: TaskStatus,
        priority: TaskPriority,
        dueDate: LocalDate?
    ): Task? {
        val project = projectRepository.findById(projectId).orElse(null) ?: return null
        val assignee = if (assigneeId != null) userRepository.findById(assigneeId).orElse(null) else null
        
        // Use demo user for now
        val createdById = "demo-user"
        
        val task = Task(
            title = title,
            description = description,
            project = project,
            assignee = assignee,
            createdBy = createdById,
            status = status,
            priority = priority,
            dueDate = dueDate
        )
        
        return taskRepository.save(task)
    }
    
    @Transactional
    fun updateTask(
        id: String,
        title: String?,
        description: String?,
        assigneeId: String?,
        status: TaskStatus?,
        priority: TaskPriority?,
        dueDate: LocalDate?
    ): Task? {
        val task = taskRepository.findById(id).orElse(null) ?: return null
        
        title?.let { task.title = it }
        description?.let { task.description = it }
        
        if (assigneeId != null) {
            val assignee = userRepository.findById(assigneeId).orElse(null)
            task.assignee = assignee
        }
        
        status?.let { task.status = it }
        priority?.let { task.priority = it }
        dueDate?.let { task.dueDate = it }
        
        task.updatedAt = LocalDateTime.now()
        
        return taskRepository.save(task)
    }
    
    @Transactional
    fun updateTaskStatus(id: String, status: TaskStatus): Task? {
        val task = taskRepository.findById(id).orElse(null) ?: return null
        task.status = status
        task.updatedAt = LocalDateTime.now()
        return taskRepository.save(task)
    }
    
    @Transactional
    fun deleteTask(id: String): Boolean {
        if (!taskRepository.existsById(id)) {
            return false
        }
        taskRepository.deleteById(id)
        return true
    }
}