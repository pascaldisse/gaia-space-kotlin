package com.gaiaspace.config

import com.gaiaspace.domain.model.*
import com.gaiaspace.domain.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
import java.time.LocalDateTime

@Configuration
class DataInitializer {

    @Bean
    @Profile("dev")
    fun initializeData(
        userRepository: UserRepository,
        workspaceRepository: WorkspaceRepository,
        projectRepository: ProjectRepository,
        taskRepository: TaskRepository,
        passwordEncoder: PasswordEncoder
    ): CommandLineRunner {
        return CommandLineRunner {
            // Create a test admin user if it doesn't exist
            val adminUser = if (!userRepository.existsByUsername("admin")) {
                val user = User(
                    username = "admin",
                    email = "admin@example.com",
                    displayName = "Admin User",
                    password = passwordEncoder.encode("password"),
                    createdAt = LocalDateTime.now(),
                    roles = setOf("ADMIN", "USER")
                )
                userRepository.save(user)
                println("Created admin user: ${user.username}")
                user
            } else {
                userRepository.findByUsername("admin").get()
            }
            
            // Create a test user if it doesn't exist
            val regularUser = if (!userRepository.existsByUsername("user")) {
                val user = User(
                    username = "user",
                    email = "user@example.com",
                    displayName = "Regular User",
                    password = passwordEncoder.encode("password"),
                    createdAt = LocalDateTime.now(),
                    roles = setOf("USER")
                )
                userRepository.save(user)
                println("Created regular user: ${user.username}")
                user
            } else {
                userRepository.findByUsername("user").get()
            }
            
            // Create a demo user for tasks
            val demoUser = if (!userRepository.existsByUsername("demo-user")) {
                val user = User(
                    username = "demo-user",
                    email = "demo@example.com",
                    displayName = "Demo User",
                    password = passwordEncoder.encode("password"),
                    createdAt = LocalDateTime.now(),
                    roles = setOf("USER")
                )
                userRepository.save(user)
                println("Created demo user: ${user.username}")
                user
            } else {
                userRepository.findByUsername("demo-user").get()
            }
            
            // Create a workspace if it doesn't exist
            val workspace = if (workspaceRepository.findAll().isEmpty()) {
                val newWorkspace = Workspace(
                    name = "Demo Workspace",
                    description = "A workspace for demonstration",
                    createdBy = adminUser.id,
                    avatarUrl = "https://via.placeholder.com/150"
                )
                val savedWorkspace = workspaceRepository.save(newWorkspace)
                
                // Add workspace members
                val adminMember = WorkspaceMember(
                    workspace = savedWorkspace,
                    user = adminUser,
                    role = "ADMIN"
                )
                val regularMember = WorkspaceMember(
                    workspace = savedWorkspace,
                    user = regularUser,
                    role = "MEMBER"
                )
                val demoMember = WorkspaceMember(
                    workspace = savedWorkspace,
                    user = demoUser,
                    role = "MEMBER"
                )
                
                savedWorkspace.members.add(adminMember)
                savedWorkspace.members.add(regularMember)
                savedWorkspace.members.add(demoMember)
                
                val result = workspaceRepository.save(savedWorkspace)
                println("Created workspace: ${result.name} with ID: ${result.id}")
                result
            } else {
                workspaceRepository.findAll().first()
            }
            
            // Create a project if it doesn't exist
            val project = if (projectRepository.findByWorkspaceId(workspace.id).isEmpty()) {
                val newProject = Project(
                    name = "Demo Project",
                    description = "A project for demonstration",
                    workspace = workspace,
                    createdBy = adminUser.id,
                    status = ProjectStatus.ACTIVE,
                    dueDate = LocalDate.now().plusMonths(1),
                    icon = "🚀"
                )
                val result = projectRepository.save(newProject)
                println("Created project: ${result.name} with ID: ${result.id}")
                result
            } else {
                projectRepository.findByWorkspaceId(workspace.id).first()
            }
            
            // Create some tasks if they don't exist
            if (taskRepository.findByProjectId(project.id).isEmpty()) {
                val task1 = Task(
                    title = "Implement login page",
                    description = "Create a login page with username and password fields",
                    project = project,
                    assignee = regularUser,
                    createdBy = demoUser.id,
                    status = TaskStatus.TODO,
                    priority = TaskPriority.HIGH,
                    dueDate = LocalDate.now().plusDays(5)
                )
                
                val task2 = Task(
                    title = "Design dashboard",
                    description = "Create wireframes for the dashboard",
                    project = project,
                    assignee = adminUser,
                    createdBy = demoUser.id,
                    status = TaskStatus.IN_PROGRESS,
                    priority = TaskPriority.MEDIUM,
                    dueDate = LocalDate.now().plusDays(3)
                )
                
                val task3 = Task(
                    title = "Set up database",
                    description = "Configure database connections and schemas",
                    project = project,
                    assignee = demoUser,
                    createdBy = adminUser.id,
                    status = TaskStatus.DONE,
                    priority = TaskPriority.CRITICAL,
                    dueDate = LocalDate.now().minusDays(1)
                )
                
                taskRepository.saveAll(listOf(task1, task2, task3))
                println("Created 3 tasks for project: ${project.name}")
            }
        }
    }
}