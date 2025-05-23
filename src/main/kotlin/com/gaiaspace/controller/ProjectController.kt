package com.gaiaspace.controller

import com.gaiaspace.domain.model.Project
import com.gaiaspace.domain.model.ProjectStatus
import com.gaiaspace.domain.model.Workspace
import com.gaiaspace.domain.repository.ProjectRepository
import com.gaiaspace.domain.repository.WorkspaceRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDateTime

@Controller
@RequestMapping("/projects")
class ProjectViewController {

    @GetMapping("/new")
    fun newProjectPage(): ModelAndView {
        return ModelAndView("forward:/projects-new.html")
    }
}

data class CreateProjectRequest(
    val name: String,
    val description: String? = null,
    val workspaceId: String? = null,
    val status: String = "ACTIVE",
    val icon: String = "🚀"
)

@RestController
@RequestMapping("/api/projects")
class ProjectApiController(
    private val projectRepository: ProjectRepository,
    private val workspaceRepository: WorkspaceRepository
) {
    
    @GetMapping
    fun getAllProjects(): ResponseEntity<List<Project>> {
        return ResponseEntity.ok(projectRepository.findAll())
    }
    
    @PostMapping
    fun createProject(@RequestBody request: CreateProjectRequest): ResponseEntity<Any> {
        return try {
            // If no workspaces available, create a default one
            val workspace = if (request.workspaceId.isNullOrBlank()) {
                // Return an error message for now, as we don't want to auto-create workspaces
                // In a real app, you'd provide a better error message or UI to create a workspace
                return ResponseEntity.badRequest().body(mapOf("error" to "Please select a workspace"))
            } else {
                workspaceRepository.findById(request.workspaceId)
                    .orElseThrow { IllegalArgumentException("Workspace not found") }
            }
            
            val status = try {
                ProjectStatus.valueOf(request.status.uppercase())
            } catch (e: IllegalArgumentException) {
                ProjectStatus.ACTIVE
            }
            
            val project = Project(
                name = request.name,
                description = request.description,
                workspace = workspace,
                createdBy = "system", // In a real app, this would be the current user ID
                status = status,
                icon = request.icon,
                createdAt = LocalDateTime.now()
            )
            
            val savedProject = projectRepository.save(project)
            ResponseEntity.ok(savedProject)
        } catch (e: Exception) {
            // Log the error
            println("Error creating project: ${e.message}")
            e.printStackTrace()
            
            // Return a meaningful error message
            ResponseEntity.badRequest().body(mapOf("error" to "Failed to create project: ${e.message}"))
        }
    }
    
    @GetMapping("/{id}")
    fun getProject(@PathVariable id: String): ResponseEntity<Project> {
        val project = projectRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Project not found") }
        
        return ResponseEntity.ok(project)
    }
}