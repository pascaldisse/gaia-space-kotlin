package com.gaiaspace.dto.request

import com.gaiaspace.domain.model.TaskStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateTaskRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    val title: String,
    
    val description: String?,
    
    @field:NotBlank(message = "Project ID is required")
    val projectId: String,
    
    val assigneeId: String?,
    
    val status: TaskStatus = TaskStatus.TODO,
    
    @field:NotBlank(message = "Priority is required")
    val priority: String,
    
    val dueDate: LocalDate?
)

data class UpdateTaskRequest(
    val title: String?,
    val description: String?,
    val assigneeId: String?,
    val status: TaskStatus?,
    val priority: String?,
    val dueDate: LocalDate?
)

data class UpdateTaskStatusRequest(
    val status: TaskStatus
)

data class AddTaskCommentRequest(
    @field:NotBlank(message = "Content is required")
    @field:Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    val content: String
)

data class FilterTasksRequest(
    val projectId: String,
    val status: TaskStatus?,
    val assigneeId: String?,
    val dueDateFrom: LocalDate?,
    val dueDateTo: LocalDate?,
    val priority: String?,
    val searchTerm: String?
)