# Gaia Space Kotlin Project Guidelines

## Build and Development
- `./gradlew clean build` - Full build with tests
- `./gradlew bootRun` - Start the application
- `./gradlew bootRun --args='--spring.profiles.active=dev'` - Run in development mode
- `./gradlew bootJar` - Create executable JAR file

## Testing
- `./gradlew test` - Run all tests
- `./gradlew test --tests "com.gaiaspace.service.UserServiceTest"` - Run specific test class
- `./gradlew jacocoTestReport` - Generate test coverage report

## Database Migration
- `./gradlew flywayMigrate` - Apply all pending migrations
- `./gradlew flywayInfo` - Show migration information
- `./gradlew flywayValidate` - Validate applied migrations against available ones

## Code Style and Analysis
- `./gradlew ktlintCheck` - Check Kotlin code style
- `./gradlew ktlintFormat` - Format Kotlin code
- `./gradlew detekt` - Run static code analysis

## Git Operations
- `git branch` - List local branches
- `git checkout -b feature/name` - Create and switch to new branch
- `git commit -m "Message"` - Commit changes with message
- `git push origin feature/name` - Push branch to remote

## Code Style Guidelines
- **Imports**: Order imports: 1) Kotlin/Java standard library, 2) External libraries, 3) Project imports
- **Formatting**: Use standard Kotlin formatting (4-space indentation)
- **Classes**: Use data classes for DTOs, entities, and value objects
- **Naming**: Follow Kotlin naming conventions (CamelCase for classes, camelCase for functions/properties)
- **Components**: Single responsibility principle for services and controllers
- **Error Handling**: Use exceptions for error cases, proper error responses in controllers
- **Documentation**: Add KDoc comments for public APIs and complex logic
- **Testing**: Write unit tests for services, integration tests for repositories and controllers

## Architecture
- **Domain Layer**: Contains entities and business logic (domain/model)
- **Repository Layer**: Provides data access (domain/repository)
- **Service Layer**: Implements business logic, orchestrates operations (service)
- **Controller Layer**: Exposes API endpoints, maps between DTOs and domain (controller)
- **DTOs**: Request/response objects for API (dto/request, dto/response)
- **Security**: Authentication and authorization functionality (security)

## Major Components
- **UserService**: User management and authentication
- **WorkspaceService**: Workspace and member operations
- **ProjectService**: Project management
- **TaskService**: Task management and workflow
- **DiscordService**: Discord integration
- **PipelineService**: CI/CD pipeline functionality