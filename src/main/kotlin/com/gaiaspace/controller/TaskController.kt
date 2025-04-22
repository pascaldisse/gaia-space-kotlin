package com.gaiaspace.controller

import com.gaiaspace.domain.model.TaskPriority
import com.gaiaspace.domain.model.TaskStatus
import com.gaiaspace.dto.request.AddTaskCommentRequest
import com.gaiaspace.dto.request.CreateTaskRequest
import com.gaiaspace.dto.request.FilterTasksRequest
import com.gaiaspace.dto.request.UpdateTaskRequest
import com.gaiaspace.dto.request.UpdateTaskStatusRequest
import com.gaiaspace.dto.response.TaskCommentResponse
import com.gaiaspace.dto.response.TaskResponse
import com.gaiaspace.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {
    
    @GetMapping
    // @PreAuthorize("isAuthenticated()") - Removed temporarily for demo
    fun getTasks(
        @RequestParam(required = false) projectId: String?,
        @RequestParam(required = false) status: TaskStatus?,
        @RequestParam(required = false) assigneeId: String?,
        @RequestParam(required = false) dueDateFrom: LocalDate?,
        @RequestParam(required = false) dueDateTo: LocalDate?,
        @RequestParam(required = false) priority: String?,
        @RequestParam(required = false) searchTerm: String?
    ): ResponseEntity<List<TaskResponse>> {
        val tasks = if (projectId != null) {
            when {
                // Apply filters in order of precedence
                searchTerm != null -> taskService.searchTasksByTitle(projectId, searchTerm)
                status != null -> taskService.findByProjectIdAndStatus(projectId, status)
                assigneeId != null -> taskService.findByAssigneeId(assigneeId)
                dueDateFrom != null && dueDateTo != null -> taskService.findByDueDateRange(dueDateFrom, dueDateTo)
                else -> taskService.findByProjectId(projectId)
            }
        } else {
            // Return all tasks if no project ID specified
            taskService.findAll()
        }
        
        return ResponseEntity.ok(tasks.map { TaskResponse.fromTask(it) })
    }
    
    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    fun countTasks(
        @RequestParam(required = false) projectId: String?,
        @RequestParam(required = false) status: TaskStatus?
    ): ResponseEntity<Map<String, Long>> {
        val count = when {
            projectId != null && status != null -> taskService.countTasksByProjectIdAndStatus(projectId, status)
            projectId != null -> taskService.countTasksByProjectId(projectId)
            else -> taskService.countAll()
        }
        
        return ResponseEntity.ok(mapOf("count" to count))
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getTaskById(@PathVariable id: String): ResponseEntity<TaskResponse> {
        val task = taskService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(TaskResponse.fromTask(task))
    }
    
    @PostMapping
    // @PreAuthorize("isAuthenticated()") - Removed temporarily for demo
    fun createTask(
        @RequestBody request: CreateTaskRequest
    ): ResponseEntity<TaskResponse> {
        // For demo, use a default user ID
        val userId = "demo-user" 
        
        val priority = try {
            TaskPriority.valueOf(request.priority)
        } catch (e: IllegalArgumentException) {
            TaskPriority.MEDIUM
        }
        
        val task = taskService.createTask(
            title = request.title,
            description = request.description,
            projectId = request.projectId,
            assigneeId = request.assigneeId,
            status = request.status,
            priority = priority,
            dueDate = request.dueDate
        ) ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TaskResponse.fromTask(task))
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun updateTask(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateTaskRequest
    ): ResponseEntity<TaskResponse> {
        val priority = request.priority?.let {
            try {
                TaskPriority.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
        
        val task = taskService.updateTask(
            id = id,
            title = request.title,
            description = request.description,
            assigneeId = request.assigneeId,
            status = request.status,
            priority = priority,
            dueDate = request.dueDate
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(TaskResponse.fromTask(task))
    }
    
    @PutMapping("/{id}/status")
    // @PreAuthorize("isAuthenticated()") - Removed temporarily for demo
    fun updateTaskStatus(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateTaskStatusRequest
    ): ResponseEntity<TaskResponse> {
        val task = taskService.updateTaskStatus(id, request.status)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        
        return ResponseEntity.ok(TaskResponse.fromTask(task))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun deleteTask(@PathVariable id: String): ResponseEntity<Void> {
        val success = taskService.deleteTask(id)
        
        return if (success) ResponseEntity.noContent().build()
        else ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
}