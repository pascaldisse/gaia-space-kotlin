// Task View Management JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Check if there's a recently created task notification
    const taskCreated = sessionStorage.getItem('task-created');
    const taskTitle = sessionStorage.getItem('task-title');
    
    if (taskCreated === 'true' && taskTitle) {
        // Show notification
        showFilterNotification(`Task "${taskTitle}" created successfully!`, 'success');
        
        // Clear the notification flags
        sessionStorage.removeItem('task-created');
        sessionStorage.removeItem('task-title');
    }
    
    // Load tasks from localStorage and add them to the list
    loadTasksFromStorage();
    // Task View Switching (List/Grid)
    const listViewBtn = document.getElementById('list-view');
    const gridViewBtn = document.getElementById('grid-view');
    const taskList = document.getElementById('task-list');
    
    if (listViewBtn && gridViewBtn && taskList) {
        listViewBtn.addEventListener('click', function() {
            taskList.classList.remove('grid-view');
            listViewBtn.classList.add('active');
            gridViewBtn.classList.remove('active');
            // Store preference in localStorage
            localStorage.setItem('taskView', 'list');
            // Update display for all visible tasks
            updateTaskDisplay();
        });
        
        gridViewBtn.addEventListener('click', function() {
            taskList.classList.add('grid-view');
            gridViewBtn.classList.add('active');
            listViewBtn.classList.remove('active');
            // Store preference in localStorage
            localStorage.setItem('taskView', 'grid');
            // Update display for all visible tasks
            updateTaskDisplay();
        });
        
        // Check for saved preference
        const savedView = localStorage.getItem('taskView');
        if (savedView === 'grid') {
            taskList.classList.add('grid-view');
            gridViewBtn.classList.add('active');
            listViewBtn.classList.remove('active');
        }
    }
    
    // Add data attributes to tasks for filtering
    initializeTaskDataAttributes();
    
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
            
            // Show filter cleared message
            showFilterNotification('All filters have been cleared!', 'info');
        });
    }
    
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            applyFilters();
            
            // Hide filters panel
            taskFilters.classList.remove('active');
            filterBtn.textContent = 'Filter';
            
            // Show filter applied message
            showFilterNotification('Filters applied successfully!', 'success');
        });
    }
    
    // Sort Tasks
    const sortSelect = document.getElementById('sort-tasks');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            sortTasks(this.value);
            showFilterNotification('Tasks sorted successfully!', 'info');
        });
    }
    
    // Task Checkbox functionality
    const taskCheckboxes = document.querySelectorAll('.task-checkbox input');
    taskCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const taskItem = this.closest('.task-item');
            if (this.checked) {
                taskItem.classList.add('completed');
                taskItem.setAttribute('data-status', 'completed');
                // In a real app, you would send an API request to update the task status
                console.log('Task marked as completed:', taskItem.querySelector('.task-title').textContent);
                showFilterNotification(`Task "${taskItem.querySelector('.task-title').textContent}" marked as completed!`, 'success');
            } else {
                taskItem.classList.remove('completed');
                taskItem.setAttribute('data-status', taskItem.getAttribute('data-original-status') || 'open');
                console.log('Task marked as incomplete:', taskItem.querySelector('.task-title').textContent);
                showFilterNotification(`Task "${taskItem.querySelector('.task-title').textContent}" marked as in progress!`, 'info');
            }
        });
    });
    
    // Task Actions (Edit buttons)
    const editBtns = document.querySelectorAll('.task-actions .btn');
    editBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const taskItem = this.closest('.task-item');
            const taskTitle = taskItem.querySelector('.task-title').textContent;
            
            // If the button is a link (has href), don't show alert
            if (!this.getAttribute('href')) {
                alert(`Editing task: ${taskTitle}`);
            }
            // In a real app, you would open a modal or navigate to the edit page
        });
    });
});

// Initialize data attributes on task items for filtering
function initializeTaskDataAttributes() {
    const tasks = document.querySelectorAll('.task-item');
    
    tasks.forEach(task => {
        // Set Priority
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
        
        // Set Status (based on completion or explicitly set)
        if (task.classList.contains('completed')) {
            task.setAttribute('data-status', 'completed');
        } else {
            // Check for status in the task content or default to 'open'
            const statusText = task.textContent.toLowerCase();
            if (statusText.includes('in progress')) {
                task.setAttribute('data-status', 'in-progress');
            } else if (statusText.includes('in review')) {
                task.setAttribute('data-status', 'review');
            } else {
                task.setAttribute('data-status', 'open');
            }
        }
        
        // Store original status for toggling completion
        task.setAttribute('data-original-status', task.getAttribute('data-status'));
        
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
        } else {
            // Try to infer from context
            const taskContent = task.textContent.toLowerCase();
            if (taskContent.includes('api') || taskContent.includes('database') || taskContent.includes('server')) {
                task.setAttribute('data-project', '1'); // Backend
            } else if (taskContent.includes('ui') || taskContent.includes('interface') || taskContent.includes('design')) {
                task.setAttribute('data-project', '2'); // Frontend
            } else {
                task.setAttribute('data-project', 'all');
            }
        }
        
        // Set assignee (this would come from the backend in a real app)
        // For demo, assign randomly
        const assignees = ['1', '2', '3'];
        task.setAttribute('data-assignee', assignees[Math.floor(Math.random() * assignees.length)]);
    });
}

// Apply filters to task list
function applyFilters() {
    const statusFilter = document.getElementById('status-filter').value;
    const priorityFilter = document.getElementById('priority-filter').value;
    const projectFilter = document.getElementById('project-filter').value;
    const assigneeFilter = document.getElementById('assignee-filter') ? 
                          document.getElementById('assignee-filter').value : 'all';
    
    const tasks = document.querySelectorAll('.task-item');
    let visibleCount = 0;
    
    tasks.forEach(task => {
        const statusMatch = statusFilter === 'all' || task.getAttribute('data-status') === statusFilter;
        const priorityMatch = priorityFilter === 'all' || task.getAttribute('data-priority') === priorityFilter;
        const projectMatch = projectFilter === 'all' || task.getAttribute('data-project') === projectFilter;
        const assigneeMatch = assigneeFilter === 'all' || task.getAttribute('data-assignee') === assigneeFilter;
        
        // Make task visible only if it matches all selected filters
        const isVisible = statusMatch && priorityMatch && projectMatch && assigneeMatch;
        
        if (isVisible) {
            const taskList = document.getElementById('task-list');
            task.style.display = taskList && taskList.classList.contains('grid-view') ? 'block' : 'flex';
            visibleCount++;
        } else {
            task.style.display = 'none';
        }
    });
    
    // If no tasks match filters, show a message
    if (visibleCount === 0) {
        showFilterNotification('No tasks match the selected filters.', 'warning');
    }
    
    return visibleCount;
}

// Update task display based on current view mode
function updateTaskDisplay() {
    const taskList = document.getElementById('task-list');
    const isGridView = taskList && taskList.classList.contains('grid-view');
    
    const visibleTasks = document.querySelectorAll('.task-item[style*="display: flex"], .task-item[style*="display: block"]');
    visibleTasks.forEach(task => {
        task.style.display = isGridView ? 'block' : 'flex';
    });
}

// Sort tasks by different criteria
function sortTasks(sortValue) {
    const tasks = Array.from(document.querySelectorAll('.task-item'));
    
    // Sort tasks based on selected option
    tasks.sort((a, b) => {
        if (sortValue === 'name-asc') {
            const titleA = a.querySelector('.task-title').textContent.toLowerCase();
            const titleB = b.querySelector('.task-title').textContent.toLowerCase();
            return titleA.localeCompare(titleB);
        } else if (sortValue === 'name-desc') {
            const titleA = a.querySelector('.task-title').textContent.toLowerCase();
            const titleB = b.querySelector('.task-title').textContent.toLowerCase();
            return titleB.localeCompare(titleA);
        } else if (sortValue === 'priority-desc') {
            const priorityA = getPriorityValue(a.getAttribute('data-priority'));
            const priorityB = getPriorityValue(b.getAttribute('data-priority'));
            return priorityB - priorityA;
        } else if (sortValue === 'priority-asc') {
            const priorityA = getPriorityValue(a.getAttribute('data-priority'));
            const priorityB = getPriorityValue(b.getAttribute('data-priority'));
            return priorityA - priorityB;
        } else if (sortValue === 'due-date-asc' || sortValue === 'due-date-desc') {
            // Extract dates from the task due dates
            const dateA = parseDueDate(a.querySelector('.task-due'));
            const dateB = parseDueDate(b.querySelector('.task-due'));
            
            if (dateA && dateB) {
                return sortValue === 'due-date-desc' ? dateB - dateA : dateA - dateB;
            }
            return 0;
        }
        return 0;
    });
    
    // Re-append sorted tasks
    const taskListEl = document.getElementById('task-list');
    tasks.forEach(task => {
        taskListEl.appendChild(task);
    });
}

// Helper Functions
function getPriorityValue(priority) {
    if (priority === 'high') return 3;
    if (priority === 'medium') return 2;
    return 1; // low priority
}

function parseDueDate(dueDateEl) {
    if (!dueDateEl) return null;
    
    const dateText = dueDateEl.textContent;
    const match = dateText.match(/Due: (\w+ \d+, \d+)|Due: (\w+ \d+)/);
    
    if (match) {
        const dateStr = match[1] || match[2];
        return new Date(dateStr + ', 2025');
    }
    
    return null;
}

// Load tasks from localStorage and render them
function loadTasksFromStorage() {
    const tasks = JSON.parse(localStorage.getItem('gaia-tasks') || '[]');
    
    if (tasks.length > 0) {
        const taskListEl = document.getElementById('task-list');
        if (!taskListEl) return;
        
        // Add user-created tasks to the list
        tasks.forEach(task => {
            // Get project name from project ID
            let projectName = '';
            switch (task.project) {
                case '1':
                    projectName = 'Gaia Space Backend';
                    break;
                case '2':
                    projectName = 'Gaia Space Frontend';
                    break;
                case '3':
                    projectName = 'Documentation';
                    break;
                default:
                    projectName = 'Other Project';
            }
            
            // Format date (if provided)
            let dueDateFormatted = '';
            if (task.dueDate) {
                const date = new Date(task.dueDate);
                const options = { year: 'numeric', month: 'short', day: 'numeric' };
                dueDateFormatted = date.toLocaleDateString('en-US', options);
            }
            
            // Create task item HTML
            const taskHTML = `
                <li class="task-item" id="${task.id}" data-priority="${task.priority}" data-status="${task.status}" data-project="${task.project}" data-assignee="${task.assignee || 'unassigned'}">
                    <div class="task-checkbox">
                        <input type="checkbox" id="checkbox-${task.id}" ${task.status === 'completed' ? 'checked' : ''}>
                    </div>
                    <div class="task-content">
                        <h4 class="task-title">${task.title}</h4>
                        <p class="task-description">${task.description || ''}</p>
                        <div class="task-meta">
                            <span class="task-project">${projectName}</span>
                            ${dueDateFormatted ? `<span class="task-due">Due: ${dueDateFormatted}</span>` : ''}
                            <span class="task-priority priority-${task.priority}" data-value="${task.priority}">${capitalizeFirstLetter(task.priority)}</span>
                        </div>
                    </div>
                    <div class="task-actions">
                        <button class="btn btn-outline btn-sm">Edit</button>
                    </div>
                </li>
            `;
            
            // Add task to list
            taskListEl.insertAdjacentHTML('beforeend', taskHTML);
        });
        
        // Re-initialize task data attributes and event listeners
        initializeTaskDataAttributes();
        
        // Re-attach event listeners to new checkboxes
        const newCheckboxes = document.querySelectorAll('.task-checkbox input');
        newCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                const taskItem = this.closest('.task-item');
                if (this.checked) {
                    taskItem.classList.add('completed');
                    taskItem.setAttribute('data-status', 'completed');
                    
                    // Update task in localStorage
                    updateTaskStatusInStorage(taskItem.id, 'completed');
                    
                    console.log('Task marked as completed:', taskItem.querySelector('.task-title').textContent);
                    showFilterNotification(`Task "${taskItem.querySelector('.task-title').textContent}" marked as completed!`, 'success');
                } else {
                    taskItem.classList.remove('completed');
                    taskItem.setAttribute('data-status', taskItem.getAttribute('data-original-status') || 'open');
                    
                    // Update task in localStorage
                    updateTaskStatusInStorage(taskItem.id, 'open');
                    
                    console.log('Task marked as incomplete:', taskItem.querySelector('.task-title').textContent);
                    showFilterNotification(`Task "${taskItem.querySelector('.task-title').textContent}" marked as in progress!`, 'info');
                }
            });
        });
        
        // Re-attach event listeners to edit buttons
        const editBtns = document.querySelectorAll('.task-item .task-actions .btn');
        editBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                const taskItem = this.closest('.task-item');
                const taskTitle = taskItem.querySelector('.task-title').textContent;
                
                // If the button is a link (has href), don't show alert
                if (!this.getAttribute('href')) {
                    alert(`Editing task: ${taskTitle}`);
                }
                // In a real app, you would open a modal or navigate to the edit page
            });
        });
    }
}

// Update task status in localStorage
function updateTaskStatusInStorage(taskId, status) {
    let tasks = JSON.parse(localStorage.getItem('gaia-tasks') || '[]');
    const taskIndex = tasks.findIndex(task => task.id === taskId);
    
    if (taskIndex !== -1) {
        tasks[taskIndex].status = status;
        localStorage.setItem('gaia-tasks', JSON.stringify(tasks));
    }
}

// Helper function to capitalize first letter
function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function showFilterNotification(message, type = 'info') {
    // Remove any existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => {
        document.body.removeChild(notification);
    });
    
    // Create new notification
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
            try {
                document.body.removeChild(notification);
            } catch (e) {
                // Notification might have been removed already
            }
        }, 300);
    }, 3000);
}