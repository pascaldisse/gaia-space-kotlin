// Task View Management JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tasks
    initializeTasks();
    
    // Setup event listeners
    setupEventListeners();
});

function initializeTasks() {
    // Add data attributes to tasks for filtering
    const tasks = document.querySelectorAll('.task-item');
    
    tasks.forEach(task => {
        // Set Priority based on class
        const priorityEl = task.querySelector('.task-priority');
        if (priorityEl) {
            if (priorityEl.classList.contains('priority-high')) {
                task.setAttribute('data-priority', 'high');
            } else if (priorityEl.classList.contains('priority-medium')) {
                task.setAttribute('data-priority', 'medium');
            } else if (priorityEl.classList.contains('priority-low')) {
                task.setAttribute('data-priority', 'low');
            }
        }
        
        // Set Status (based on completion or default to 'open')
        if (task.classList.contains('completed')) {
            task.setAttribute('data-status', 'completed');
        } else {
            task.setAttribute('data-status', 'open');
        }
        
        // Set Project
        const projectEl = task.querySelector('.task-project');
        if (projectEl) {
            const project = projectEl.textContent.trim();
            if (project.includes('Backend')) {
                task.setAttribute('data-project', '1');
            } else if (project.includes('Frontend')) {
                task.setAttribute('data-project', '2');
            } else {
                task.setAttribute('data-project', '3'); // Documentation or other
            }
        }
    });
    
    // Update counts
    updateTaskCounts();
}

function setupEventListeners() {
    // Task View Switching (List/Grid)
    const listViewBtn = document.getElementById('list-view');
    const gridViewBtn = document.getElementById('grid-view');
    const taskList = document.getElementById('task-list');
    
    if (listViewBtn && gridViewBtn && taskList) {
        listViewBtn.addEventListener('click', function() {
            taskList.classList.remove('grid-view');
            listViewBtn.classList.add('active');
            gridViewBtn.classList.remove('active');
        });
        
        gridViewBtn.addEventListener('click', function() {
            taskList.classList.add('grid-view');
            gridViewBtn.classList.add('active');
            listViewBtn.classList.remove('active');
        });
    }
    
    // Task Filtering
    const filterBtn = document.querySelector('.card-actions .btn-outline');
    const taskFilters = document.getElementById('task-filters');
    const clearFiltersBtn = document.getElementById('clear-filters');
    const applyFiltersBtn = document.getElementById('apply-filters');
    
    if (filterBtn && taskFilters) {
        filterBtn.addEventListener('click', function() {
            taskFilters.classList.toggle('active');
            if (taskFilters.classList.contains('active')) {
                filterBtn.textContent = 'Hide Filters';
            } else {
                filterBtn.textContent = 'Filter';
            }
        });
    }
    
    if (clearFiltersBtn) {
        clearFiltersBtn.addEventListener('click', function() {
            // Reset all filter dropdowns
            const filterSelects = taskFilters.querySelectorAll('select');
            filterSelects.forEach(select => {
                select.value = 'all';
            });
            
            // Show all tasks
            const tasks = document.querySelectorAll('.task-item');
            tasks.forEach(task => {
                task.style.display = taskList.classList.contains('grid-view') ? 'block' : 'flex';
            });
            
            // Show notification
            showNotification('All filters have been cleared!', 'info');
        });
    }
    
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            applyFilters();
            
            // Hide filters panel
            taskFilters.classList.remove('active');
            filterBtn.textContent = 'Filter';
            
            // Show notification
            showNotification('Filters applied successfully!', 'success');
        });
    }
    
    // Task Checkboxes
    const taskCheckboxes = document.querySelectorAll('.task-checkbox input');
    taskCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const taskItem = this.closest('.task-item');
            if (this.checked) {
                taskItem.classList.add('completed');
                taskItem.setAttribute('data-status', 'completed');
                showNotification(`Task marked as completed!`, 'success');
            } else {
                taskItem.classList.remove('completed');
                taskItem.setAttribute('data-status', 'open');
                showNotification(`Task marked as open!`, 'info');
            }
            
            // Update counts after status change
            updateTaskCounts();
        });
    });
    
    // Task Edit Buttons
    const editBtns = document.querySelectorAll('.task-actions .btn');
    editBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const taskItem = this.closest('.task-item');
            if (!this.getAttribute('href')) {
                openEditModal(taskItem);
            }
        });
    });
    
    // Setup modal event listeners
    setupEditModal();
    
    // Sort dropdown
    const sortSelect = document.getElementById('sort-tasks');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            sortTasks(this.value);
            showNotification('Tasks sorted successfully!', 'info');
        });
    }
}

function applyFilters() {
    const statusFilter = document.getElementById('status-filter').value;
    const priorityFilter = document.getElementById('priority-filter').value;
    const projectFilter = document.getElementById('project-filter').value;
    
    const tasks = document.querySelectorAll('.task-item');
    let visibleCount = 0;
    
    tasks.forEach(task => {
        const taskStatus = task.getAttribute('data-status') || 'open';
        const taskPriority = task.getAttribute('data-priority') || 'medium';
        const taskProject = task.getAttribute('data-project') || 'all';
        
        const statusMatch = statusFilter === 'all' || taskStatus === statusFilter;
        const priorityMatch = priorityFilter === 'all' || taskPriority === priorityFilter;
        const projectMatch = projectFilter === 'all' || taskProject === projectFilter;
        
        // Show task only if it matches all filters
        const isVisible = statusMatch && priorityMatch && projectMatch;
        
        if (isVisible) {
            const taskList = document.getElementById('task-list');
            task.style.display = taskList && taskList.classList.contains('grid-view') ? 'block' : 'flex';
            visibleCount++;
        } else {
            task.style.display = 'none';
        }
    });
    
    // Update counts with filtered tasks
    updateFilteredCounts();
    
    return visibleCount;
}

function updateFilteredCounts() {
    // Get visible tasks only
    const visibleTasks = document.querySelectorAll('.task-item[style*="display: flex"], .task-item[style*="display: block"]');
    
    // Update counts in sidebar
    const totalElement = document.querySelector('.stat-value:nth-of-type(1)');
    if (totalElement) {
        totalElement.textContent = visibleTasks.length;
    }
    
    // Count open tasks
    const openTasks = Array.from(visibleTasks).filter(task => 
        task.getAttribute('data-status') === 'open').length;
    
    const openElement = document.querySelector('.stat-value:nth-of-type(2)');
    if (openElement) {
        openElement.textContent = openTasks;
    }
    
    // Count in-progress tasks
    const inProgressTasks = Array.from(visibleTasks).filter(task => 
        task.getAttribute('data-status') === 'in-progress').length;
    
    const inProgressElement = document.querySelector('.stat-value:nth-of-type(3)');
    if (inProgressElement) {
        inProgressElement.textContent = inProgressTasks;
    }
    
    // Count completed tasks
    const completedTasks = Array.from(visibleTasks).filter(task => 
        task.getAttribute('data-status') === 'completed').length;
    
    const completedElement = document.querySelector('.stat-value:nth-of-type(4)');
    if (completedElement) {
        completedElement.textContent = completedTasks;
    }
}

function updateTaskCounts() {
    // Get all tasks
    const allTasks = document.querySelectorAll('.task-item');
    
    // Update total
    const totalElement = document.querySelector('.stat-value:nth-of-type(1)');
    if (totalElement) {
        totalElement.textContent = allTasks.length;
    }
    
    // Count open
    const openTasks = Array.from(allTasks).filter(task => 
        task.getAttribute('data-status') === 'open').length;
    
    const openElement = document.querySelector('.stat-value:nth-of-type(2)');
    if (openElement) {
        openElement.textContent = openTasks;
    }
    
    // Count in-progress
    const inProgressTasks = Array.from(allTasks).filter(task => 
        task.getAttribute('data-status') === 'in-progress').length;
    
    const inProgressElement = document.querySelector('.stat-value:nth-of-type(3)');
    if (inProgressElement) {
        inProgressElement.textContent = inProgressTasks;
    }
    
    // Count completed
    const completedTasks = Array.from(allTasks).filter(task => 
        task.getAttribute('data-status') === 'completed').length;
    
    const completedElement = document.querySelector('.stat-value:nth-of-type(4)');
    if (completedElement) {
        completedElement.textContent = completedTasks;
    }
}

function sortTasks(sortValue) {
    const tasks = Array.from(document.querySelectorAll('.task-item'));
    
    // Sort based on selected option
    tasks.sort((a, b) => {
        if (sortValue === 'name-asc' || sortValue === 'name-desc') {
            const titleA = a.querySelector('.task-title').textContent.toLowerCase();
            const titleB = b.querySelector('.task-title').textContent.toLowerCase();
            return sortValue === 'name-asc' ? 
                titleA.localeCompare(titleB) : 
                titleB.localeCompare(titleA);
        } 
        else if (sortValue === 'priority-asc' || sortValue === 'priority-desc') {
            const priorityMap = { 'high': 3, 'medium': 2, 'low': 1 };
            const priorityA = priorityMap[a.getAttribute('data-priority') || 'medium'];
            const priorityB = priorityMap[b.getAttribute('data-priority') || 'medium'];
            return sortValue === 'priority-asc' ? priorityA - priorityB : priorityB - priorityA;
        }
        return 0;
    });
    
    // Re-append sorted tasks
    const taskListEl = document.getElementById('task-list');
    tasks.forEach(task => {
        taskListEl.appendChild(task);
    });
}

function showNotification(message, type = 'info') {
    // Create notification
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);
    
    // Animate in
    setTimeout(() => {
        notification.classList.add('visible');
    }, 10);
    
    // Remove after 3 seconds
    setTimeout(() => {
        notification.classList.remove('visible');
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// Edit Modal Functions
function setupEditModal() {
    const modal = document.getElementById('edit-task-modal');
    const closeBtn = document.querySelector('.close-modal');
    const cancelBtn = document.getElementById('cancel-edit');
    const editForm = document.getElementById('edit-task-form');
    
    // Close modal when clicking X
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            modal.style.display = 'none';
        });
    }
    
    // Close modal when clicking Cancel
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            modal.style.display = 'none';
        });
    }
    
    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
    
    // Handle form submission
    if (editForm) {
        editForm.addEventListener('submit', function(event) {
            event.preventDefault();
            
            // Get form values
            const taskId = document.getElementById('edit-task-id').value;
            const title = document.getElementById('edit-task-title').value;
            const description = document.getElementById('edit-task-description').value;
            const project = document.getElementById('edit-task-project').value;
            const priority = document.getElementById('edit-task-priority').value;
            const status = document.getElementById('edit-task-status').value;
            
            // Find the task in the DOM
            const taskItem = document.getElementById(taskId);
            if (!taskItem) {
                showNotification('Could not find task to update', 'error');
                return;
            }
            
            // Get project name based on value
            let projectName = 'Gaia Space Backend';
            if (project === '2') projectName = 'Gaia Space Frontend';
            if (project === '3') projectName = 'Documentation';
            
            // Update the task in the DOM
            taskItem.querySelector('.task-title').textContent = title;
            taskItem.querySelector('.task-description').textContent = description || '';
            taskItem.querySelector('.task-project').textContent = projectName;
            
            // Update the priority element
            const priorityEl = taskItem.querySelector('.task-priority');
            if (priorityEl) {
                priorityEl.className = `task-priority priority-${priority}`;
                priorityEl.setAttribute('data-value', priority);
                priorityEl.textContent = capitalizeFirstLetter(priority);
            }
            
            // Update data attributes
            taskItem.setAttribute('data-priority', priority);
            taskItem.setAttribute('data-project', project);
            taskItem.setAttribute('data-status', status);
            
            // Update checkbox state if status changed
            const checkbox = taskItem.querySelector('.task-checkbox input');
            if (checkbox) {
                checkbox.checked = status === 'completed';
                
                // Update task appearance based on status
                if (status === 'completed') {
                    taskItem.classList.add('completed');
                } else {
                    taskItem.classList.remove('completed');
                }
            }
            
            // Close the modal
            modal.style.display = 'none';
            
            // Show success notification
            showNotification('Task updated successfully!', 'success');
            
            // Update task counts
            updateTaskCounts();
        });
    }
}

function openEditModal(taskItem) {
    const modal = document.getElementById('edit-task-modal');
    if (!modal) return;
    
    // Extract task data
    const taskId = taskItem.id;
    const title = taskItem.querySelector('.task-title').textContent;
    const description = taskItem.querySelector('.task-description').textContent;
    const priority = taskItem.getAttribute('data-priority') || 'medium';
    const project = taskItem.getAttribute('data-project') || '1';
    const status = taskItem.getAttribute('data-status') || 'open';
    
    // Populate the form
    document.getElementById('edit-task-id').value = taskId;
    document.getElementById('edit-task-title').value = title;
    document.getElementById('edit-task-description').value = description;
    document.getElementById('edit-task-priority').value = priority;
    document.getElementById('edit-task-project').value = project;
    document.getElementById('edit-task-status').value = status;
    
    // Display the modal
    modal.style.display = 'block';
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}