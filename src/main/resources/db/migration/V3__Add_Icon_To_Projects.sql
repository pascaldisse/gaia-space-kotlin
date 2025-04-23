-- Add icon column to projects table if it doesn't exist
ALTER TABLE projects ADD COLUMN IF NOT EXISTS icon VARCHAR(10) DEFAULT '🚀';