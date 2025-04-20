# Gaia Space - Kotlin Backend

Gaia Space is an open-source collaborative workspace platform built with Kotlin and Spring Boot. It provides a comprehensive environment for team communication, project management, document sharing, and third-party service integrations like Discord.

## Features

- **Team Collaboration** - Workspaces, channels, and real-time messaging
- **Project Management** - Kanban boards, tasks, priorities, deadlines, and task dependencies
- **Document Sharing** - Markdown support and document versioning
- **Source Control** - Git repositories, branches, and merge requests
- **CI/CD Pipelines** - Automated builds, tests, and deployments
- **Discord Integration** - Connect Discord servers to workspaces
- **RESTful API** - Comprehensive API for frontend and integrations
- **GraphQL Support** - Advanced queries for complex data requirements
- **Secure Authentication** - JWT-based authentication and role-based access control

## Tech Stack

- **Language:** Kotlin
- **Framework:** Spring Boot 3.2
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA
- **Security:** Spring Security with JWT
- **API Documentation:** OpenAPI (Swagger)
- **Migration:** Flyway
- **Testing:** JUnit, Mockito, MockK
- **Discord Integration:** Discord4J

## System Requirements

- JDK 17 or later
- PostgreSQL 13 or later
- Git

## Building and Running

### Prerequisites

1. Install JDK 17 or later
2. Install PostgreSQL and create a database named `gaia_space`
3. Clone this repository:

```bash
git clone https://github.com/gaia-space/gaia-space-kotlin.git
cd gaia-space-kotlin
```

### Configuration

Create an `.env` file in the project root with the following environment variables:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gaia_space
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Discord OAuth2 credentials
DISCORD_CLIENT_ID=your_discord_client_id
DISCORD_CLIENT_SECRET=your_discord_client_secret
DISCORD_REDIRECT_URI=http://localhost:8080/api/auth/discord/callback
```

### Building the Project

```bash
./gradlew clean build
```

### Running the Application

```bash
./gradlew bootRun
```

For development mode:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The server will start on http://localhost:8080

## API Documentation

Once the application is running, you can access the API documentation at:

- http://localhost:8080/swagger-ui.html
- http://localhost:8080/v3/api-docs

## Project Structure

```
src/main/kotlin/com/gaiaspace/
├── GaiaSpaceApplication.kt       # Application entry point
├── controller/                   # REST Controllers
├── domain/
│   ├── model/                    # JPA Entities
│   └── repository/               # Data Repositories
├── dto/
│   ├── request/                  # API Request DTOs
│   └── response/                 # API Response DTOs
├── service/                      # Business Logic Services
├── graphql/                      # GraphQL Controllers
├── security/                     # Security Configuration
└── config/                       # Application Configuration

src/main/resources/
├── application.yml               # Application Configuration
├── application-dev.yml           # Development Configuration
├── graphql/                      # GraphQL Schema Definitions
└── db/migration/                 # Flyway Database Migrations
```

## Database Schema

The application uses a comprehensive database schema with the following main components:

- Users and Authentication
- Workspaces and Membership
- Channels and Messaging
- Projects and Tasks
- Boards and Kanban
- Documents and Versioning
- Git Repositories and Merge Requests
- CI/CD Pipelines and Jobs
- Discord Integration

Database migrations are managed with Flyway and can be found in `src/main/resources/db/migration/`.

## API Endpoints

### Authentication
- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - User login
- `GET /api/users/me` - Get current user profile

### Workspaces
- `GET /api/workspaces` - List all workspaces for user
- `POST /api/workspaces` - Create a new workspace
- `GET /api/workspaces/{id}` - Get workspace details
- `PUT /api/workspaces/{id}` - Update workspace
- `DELETE /api/workspaces/{id}` - Delete workspace
- `GET /api/workspaces/{id}/members` - List workspace members
- `POST /api/workspaces/{id}/members` - Add a member to workspace

### CI/CD Pipelines
- `GET /api/pipelines/{id}` - Get pipeline details
- `GET /api/pipelines/repository/{repositoryId}` - List pipelines for a repository
- `POST /api/pipelines` - Create a pipeline
- `PUT /api/pipelines/{id}` - Update pipeline
- `POST /api/pipelines/{id}/jobs` - Add a job to pipeline
- `POST /api/pipelines/jobs/{jobId}/steps` - Add a step to job
- `POST /api/pipelines/{id}/runs` - Create a pipeline run
- `GET /api/pipelines/{id}/runs` - List pipeline runs
- `GET /api/pipelines/runs/{runId}` - Get pipeline run details

### Discord Integration
- `POST /api/discord/integrations` - Create a new Discord integration
- `GET /api/discord/integrations/workspace/{id}` - List Discord integrations for workspace
- `GET /api/discord/integrations/{id}/channels` - List Discord channels for integration
- `POST /api/discord/integrations/{id}/channel-mappings` - Add a Discord channel mapping

## GraphQL API

The application also provides a GraphQL API for more complex data requirements. The GraphQL schema defines types for all major entities and provides queries and mutations for common operations.

Example GraphQL query:
```graphql
query {
  workspaceWithDetails(id: "workspace-id") {
    id
    name
    members {
      user {
        username
        displayName
      }
      role
    }
    projects {
      name
      status
      tasks {
        title
        status
        assignee {
          displayName
        }
      }
    }
  }
}
```

## Testing

Run tests with:

```bash
./gradlew test
```

## Contributing

We welcome contributions to Gaia Space! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add amazing feature'`)
5. Push to your branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.