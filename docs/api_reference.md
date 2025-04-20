# API Reference

## Core Models

### User

Represents a user account in the system.

```dart
class User {
  final String id;
  final String username;
  final String? email;
  final String? displayName;
  final String? avatarUrl;
  final DateTime createdAt;
}
```

### Workspace

Represents a collaborative workspace environment.

```dart
class Workspace {
  final String id;
  final String name;
  final String? description;
  final String createdBy;
  final DateTime createdAt;
  final int membersCount;
  final int channelsCount;
  final String? avatarUrl;
}
```

### Project

Represents a project within a workspace.

```dart
class Project {
  final String id;
  final String name;
  final String? description;
  final String workspaceId;
  final String createdBy;
  final DateTime createdAt;
  final DateTime? updatedAt;
  final String? status;
  final List<String>? tags;
  final DateTime? dueDate;
}
```

### Task

Represents a task within a project.

```dart
class Task {
  final String id;
  final String title;
  final String? description;
  final String status;
  final String priority;
  final String workspaceId;
  final String projectId;
  final String? assigneeId;
  final String createdBy;
  final DateTime createdAt;
  final DateTime? updatedAt;
  final DateTime? dueDate;
  final List<String> tagIds;
  final List<String> dependsOnTaskIds;
}
```

### Repository

Represents a code repository connected to the system.

```dart
class Repository {
  final String id;
  final String name;
  final String? description;
  final String workspaceId;
  final String url;
  final String provider; // e.g., "github", "gitlab"
  final bool isPrivate;
  final DateTime createdAt;
  final DateTime? updatedAt;
}
```

### Document

Represents a document in the system.

```dart
class Document {
  final String id;
  final String title;
  final String? content;
  final String workspaceId;
  final String? projectId;
  final String createdBy;
  final DateTime createdAt;
  final DateTime? updatedAt;
  final List<String>? tags;
}
```

### DiscordIntegration

Represents a connection to a Discord server.

```dart
class DiscordIntegration {
  final String id;
  final String workspaceId;
  final String serverId;
  final String serverName;
  final Map<String, String>? channelMappings;
  final DateTime createdAt;
  final DateTime? updatedAt;
  final bool isActive;
}
```

## Core Services

### AuthService

Handles user authentication and authorization.

```dart
class AuthService {
  static User? get currentUser;
  static bool get isAuthenticated;
  
  static Future<User?> login(String username, String password);
  static Future<User?> register(String username, String email, String password);
  static Future<void> logout();
  static Future<bool> resetPassword(String email);
}
```

### DiscordService

Manages Discord integration with workspaces.

```dart
class DiscordService {
  Future<List<DiscordIntegration>> getWorkspaceIntegrations(String workspaceId);
  Future<DiscordIntegration> createIntegration(String workspaceId, String token);
  Future<DiscordIntegration> updateIntegration(DiscordIntegration integration);
  Future<bool> deleteIntegration(String integrationId);
}
```

### NavigationService

Provides navigation capabilities throughout the app.

```dart
class NavigationService {
  static GlobalKey<NavigatorState> navigatorKey;
  
  static Future<dynamic> navigateTo(String routeName, {Object? arguments});
  static Future<dynamic> navigateToAndRemove(String routeName, {Object? arguments});
  static void goBack();
}
```

### TaskService

Handles task operations and management.

```dart
class TaskService {
  Future<List<Task>> getWorkspaceTasks(String workspaceId);
  Future<List<Task>> getProjectTasks(String projectId);
  Future<Task?> getTaskById(String id);
  Future<Task> createTask(TaskRequest request);
  Future<Task> updateTask(String id, TaskRequest request);
  Future<bool> deleteTask(String id);
  Future<List<Task>> getTaskDependencies(String taskId);
  Future<bool> addTaskDependency(String taskId, String dependsOnTaskId);
  Future<bool> removeTaskDependency(String taskId, String dependsOnTaskId);
  Future<List<TaskActivity>> getTaskActivity(String taskId);
}
```

## State Providers

### workspacesProvider

Provides access to all workspaces.

```dart
final workspacesProvider = StateNotifierProvider<WorkspaceNotifier, List<Workspace>>((ref) {
  return WorkspaceNotifier();
});
```

### selectedWorkspaceProvider

Tracks the currently selected workspace.

```dart
final selectedWorkspaceProvider = StateProvider<Workspace?>((ref) => null);
```

### workspaceViewTypeProvider

Controls the display type for workspace lists.

```dart
final workspaceViewTypeProvider = StateProvider<WorkspaceViewType>((ref) {
  return WorkspaceViewType.grid;
});
```

### filteredWorkspacesProvider

Provides filtered workspaces based on search criteria.

```dart
final filteredWorkspacesProvider = Provider<List<Workspace>>((ref) {
  final workspaces = ref.watch(workspacesProvider);
  final searchQuery = ref.watch(workspaceSearchQueryProvider);
  
  if (searchQuery.isEmpty) {
    return workspaces;
  }
  
  return workspaces.where((workspace) {
    final query = searchQuery.toLowerCase();
    return workspace.name.toLowerCase().contains(query) ||
           (workspace.description?.toLowerCase().contains(query) ?? false);
  }).toList();
});
```

### tasksProvider

Provides access to tasks within a project.

```dart
final projectTasksProvider = FutureProvider.family<List<Task>, String>((ref, projectId) async {
  final taskService = ref.read(taskServiceProvider);
  return taskService.getProjectTasks(projectId);
});
```

### taskDependenciesProvider

Provides task dependencies for a given task.

```dart
final taskDependenciesProvider = FutureProvider.family<List<Task>, String>((ref, taskId) async {
  final taskService = ref.read(taskServiceProvider);
  return taskService.getTaskDependencies(taskId);
});
```