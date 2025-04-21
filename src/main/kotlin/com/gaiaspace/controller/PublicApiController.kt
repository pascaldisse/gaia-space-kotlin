package com.gaiaspace.controller

import com.gaiaspace.domain.repository.WorkspaceRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/public")
class PublicApiController(private val workspaceRepository: WorkspaceRepository) {
    
    data class WorkspaceOption(
        val id: String,
        val name: String
    )
    
    @GetMapping("/workspaces")
    fun getWorkspaceOptions(): ResponseEntity<List<WorkspaceOption>> {
        val workspaces = workspaceRepository.findAll()
        return ResponseEntity.ok(workspaces.map { WorkspaceOption(it.id, it.name) })
    }
}