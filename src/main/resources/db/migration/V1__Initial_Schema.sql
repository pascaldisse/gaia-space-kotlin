-- Users and authentication
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE user_roles (
    user_id VARCHAR(36) REFERENCES users(id),
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Workspaces
CREATE TABLE workspaces (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    avatar_url VARCHAR(255)
);

CREATE TABLE workspace_members (
    id VARCHAR(36) PRIMARY KEY,
    workspace_id VARCHAR(36) NOT NULL REFERENCES workspaces(id),
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    role VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    UNIQUE (workspace_id, user_id)
);

-- Channels
CREATE TABLE channels (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    channel_type VARCHAR(20) NOT NULL,
    workspace_id VARCHAR(36) NOT NULL REFERENCES workspaces(id),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE messages (
    id VARCHAR(36) PRIMARY KEY,
    content TEXT NOT NULL,
    channel_id VARCHAR(36) NOT NULL REFERENCES channels(id),
    sender_id VARCHAR(36) NOT NULL REFERENCES users(id),
    reply_to_id VARCHAR(36) REFERENCES messages(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_edited BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE message_attachments (
    message_id VARCHAR(36) NOT NULL REFERENCES messages(id),
    url VARCHAR(255) NOT NULL,
    PRIMARY KEY (message_id, url)
);

CREATE TABLE message_reactions (
    message_id VARCHAR(36) NOT NULL REFERENCES messages(id),
    emoji VARCHAR(50) NOT NULL,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    PRIMARY KEY (message_id, emoji, user_id)
);

-- Projects
CREATE TABLE projects (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    workspace_id VARCHAR(36) NOT NULL REFERENCES workspaces(id),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    due_date DATE,
    avatar_url VARCHAR(255)
);

-- Boards
CREATE TABLE boards (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    project_id VARCHAR(36) NOT NULL REFERENCES projects(id),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE board_columns (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position INT NOT NULL,
    board_id VARCHAR(36) NOT NULL REFERENCES boards(id)
);

-- Tasks
CREATE TABLE tasks (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    project_id VARCHAR(36) NOT NULL REFERENCES projects(id),
    assignee_id VARCHAR(36) REFERENCES users(id),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    due_date DATE,
    estimated_hours FLOAT,
    spent_hours FLOAT
);

CREATE TABLE board_column_tasks (
    column_id VARCHAR(36) NOT NULL REFERENCES board_columns(id),
    task_id VARCHAR(36) NOT NULL REFERENCES tasks(id),
    PRIMARY KEY (column_id, task_id)
);

CREATE TABLE task_dependencies (
    id VARCHAR(36) PRIMARY KEY,
    dependent_task_id VARCHAR(36) NOT NULL REFERENCES tasks(id),
    prerequisite_task_id VARCHAR(36) NOT NULL REFERENCES tasks(id),
    dependency_type VARCHAR(20) NOT NULL,
    UNIQUE (dependent_task_id, prerequisite_task_id)
);

CREATE TABLE task_comments (
    id VARCHAR(36) PRIMARY KEY,
    content TEXT NOT NULL,
    task_id VARCHAR(36) NOT NULL REFERENCES tasks(id),
    author_id VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_edited BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE tags (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    color_hex VARCHAR(7),
    workspace_id VARCHAR(36) NOT NULL REFERENCES workspaces(id),
    UNIQUE (name, workspace_id)
);

CREATE TABLE task_tags (
    task_id VARCHAR(36) NOT NULL REFERENCES tasks(id),
    tag_id VARCHAR(36) NOT NULL REFERENCES tags(id),
    PRIMARY KEY (task_id, tag_id)
);

-- Documents
CREATE TABLE documents (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    project_id VARCHAR(36) REFERENCES projects(id),
    parent_document_id VARCHAR(36) REFERENCES documents(id),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE document_versions (
    id VARCHAR(36) PRIMARY KEY,
    version INT NOT NULL,
    content TEXT NOT NULL,
    document_id VARCHAR(36) NOT NULL REFERENCES documents(id),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    comment TEXT,
    UNIQUE (document_id, version)
);

-- Git Integration
CREATE TABLE git_repositories (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    git_url VARCHAR(255) NOT NULL,
    provider VARCHAR(20) NOT NULL,
    workspace_id VARCHAR(36) NOT NULL REFERENCES workspaces(id),
    project_id VARCHAR(36) REFERENCES projects(id),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    default_branch VARCHAR(100) NOT NULL DEFAULT 'main'
);

CREATE TABLE git_branches (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    repository_id VARCHAR(36) NOT NULL REFERENCES git_repositories(id),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    last_commit_id VARCHAR(40),
    last_commit_message TEXT,
    last_commit_date TIMESTAMP
);

CREATE TABLE merge_requests (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    repository_id VARCHAR(36) NOT NULL REFERENCES git_repositories(id),
    source_branch VARCHAR(100) NOT NULL,
    target_branch VARCHAR(100) NOT NULL,
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    merged_at TIMESTAMP,
    merged_by VARCHAR(36) REFERENCES users(id),
    closed_at TIMESTAMP,
    closed_by VARCHAR(36) REFERENCES users(id)
);

CREATE TABLE merge_request_comments (
    id VARCHAR(36) PRIMARY KEY,
    content TEXT NOT NULL,
    merge_request_id VARCHAR(36) NOT NULL REFERENCES merge_requests(id),
    file_path VARCHAR(255),
    line_number INT,
    author_id VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Discord Integration
CREATE TABLE discord_integrations (
    id VARCHAR(36) PRIMARY KEY,
    workspace_id VARCHAR(36) NOT NULL REFERENCES workspaces(id),
    discord_server_id VARCHAR(20) NOT NULL,
    discord_server_name VARCHAR(100) NOT NULL,
    access_token VARCHAR(100) NOT NULL,
    refresh_token VARCHAR(100) NOT NULL,
    token_expires_at TIMESTAMP NOT NULL,
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE discord_channel_mappings (
    id VARCHAR(36) PRIMARY KEY,
    discord_integration_id VARCHAR(36) NOT NULL REFERENCES discord_integrations(id),
    discord_channel_id VARCHAR(20) NOT NULL,
    discord_channel_name VARCHAR(100) NOT NULL,
    gaia_channel_id VARCHAR(36) NOT NULL REFERENCES channels(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    sync_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sync_direction VARCHAR(20) NOT NULL
);

-- Indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_workspace_members_workspace_id ON workspace_members(workspace_id);
CREATE INDEX idx_workspace_members_user_id ON workspace_members(user_id);
CREATE INDEX idx_channels_workspace_id ON channels(workspace_id);
CREATE INDEX idx_messages_channel_id ON messages(channel_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_projects_workspace_id ON projects(workspace_id);
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_assignee_id ON tasks(assignee_id);
CREATE INDEX idx_task_dependencies_dependent_task_id ON task_dependencies(dependent_task_id);
CREATE INDEX idx_task_dependencies_prerequisite_task_id ON task_dependencies(prerequisite_task_id);
CREATE INDEX idx_documents_project_id ON documents(project_id);
CREATE INDEX idx_git_repositories_workspace_id ON git_repositories(workspace_id);
CREATE INDEX idx_git_repositories_project_id ON git_repositories(project_id);
CREATE INDEX idx_discord_integrations_workspace_id ON discord_integrations(workspace_id);