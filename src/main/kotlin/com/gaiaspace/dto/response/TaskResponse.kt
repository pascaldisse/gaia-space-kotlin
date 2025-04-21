package com.gaiaspace.dto.response

import com.gaiaspace.domain.model.Task
import com.gaiaspace.domain.model.TaskComment
import com.gaiaspace.domain.model.TaskStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskResponse(
    val id: String,
    val title: String,
    val description: String?,
    val status: String,
    val priority: String,
    val projectId: String,
    val projectName: String,
    val assigneeId: String?,
    val assigneeName: String?,
    val dueDate: LocalDate?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val commentsCount: Int,
    val tagsCount: Int
) {
    companion object {
        fun fromTask(task: Task): TaskResponse {
            return TaskResponse(
                id = task.id,
                title = task.title,
                description = task.description,
                status = task.status.name,
                priority = task.priority.name,
                projectId = task.project.id,
                projectName = task.project.name,
                assigneeId = task.assignee?.id,
                assigneeName = task.assignee?.displayName,
                dueDate = task.dueDate,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt,
                commentsCount = task.comments.size,
                tagsCount = task.tags.size
            )
        }
    }
}

data class TaskCommentResponse(
    val id: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromTaskComment(comment: TaskComment): TaskCommentResponse {
            return TaskCommentResponse(
                id = comment.id,
                content = comment.content,
                authorId = comment.author.id,
                authorName = comment.author.displayName,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }
    }
}