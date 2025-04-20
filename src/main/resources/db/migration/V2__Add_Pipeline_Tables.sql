-- CI/CD Pipeline Tables
CREATE TABLE pipelines (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    repository_id VARCHAR(36) NOT NULL REFERENCES git_repositories(id),
    branch_pattern VARCHAR(255),
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE pipeline_jobs (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    pipeline_id VARCHAR(36) NOT NULL REFERENCES pipelines(id),
    position INT NOT NULL,
    timeout_minutes INT,
    run_condition TEXT
);

CREATE TABLE job_steps (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    job_id VARCHAR(36) NOT NULL REFERENCES pipeline_jobs(id),
    position INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    command TEXT NOT NULL,
    run_if_previous_failed BOOLEAN NOT NULL DEFAULT FALSE,
    timeout_minutes INT
);

CREATE TABLE pipeline_runs (
    id VARCHAR(36) PRIMARY KEY,
    pipeline_id VARCHAR(36) NOT NULL REFERENCES pipelines(id),
    commit_id VARCHAR(40) NOT NULL,
    branch_name VARCHAR(255) NOT NULL,
    created_by VARCHAR(36) NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE job_runs (
    id VARCHAR(36) PRIMARY KEY,
    pipeline_run_id VARCHAR(36) NOT NULL REFERENCES pipeline_runs(id),
    job_id VARCHAR(36) NOT NULL REFERENCES pipeline_jobs(id),
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE step_runs (
    id VARCHAR(36) PRIMARY KEY,
    job_run_id VARCHAR(36) NOT NULL REFERENCES job_runs(id),
    step_id VARCHAR(36) NOT NULL REFERENCES job_steps(id),
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    exit_code INT,
    output TEXT
);

-- Indexes
CREATE INDEX idx_pipelines_repository_id ON pipelines(repository_id);
CREATE INDEX idx_pipeline_jobs_pipeline_id ON pipeline_jobs(pipeline_id);
CREATE INDEX idx_job_steps_job_id ON job_steps(job_id);
CREATE INDEX idx_pipeline_runs_pipeline_id ON pipeline_runs(pipeline_id);
CREATE INDEX idx_pipeline_runs_branch_name ON pipeline_runs(branch_name);
CREATE INDEX idx_job_runs_pipeline_run_id ON job_runs(pipeline_run_id);
CREATE INDEX idx_step_runs_job_run_id ON step_runs(job_run_id);