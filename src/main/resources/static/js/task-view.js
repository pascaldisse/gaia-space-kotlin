// Task View Management JavaScript

console.log('Task view script loaded');

// Function to fetch tasks from API and update the task list
function fetchTasks() {
    console.log('Fetching tasks from API');
    // Get the current project ID from URL parameters or use a default
    const urlParams = new URLSearchParams(window.location.search);
    const projectId = urlParams.get('project') || '1';  // Default to project ID 1
    
    // Fetch all tasks
    fetchAllTasks(projectId);
}

// Fetch all tasks for a project
function fetchAllTasks(projectId) {
    // API endpoint for tasks
    console.log(`Making API request to: /api/tasks?projectId=${projectId}`);
    fetch(`/api/tasks?projectId=${projectId}`)
        .then(response => {
            console.log('API Response status:', response.status);
            if (!response.ok) {
                throw new Error(`Network response was not ok: ${response.status}`);
            }
            return response.json();
        })
        .then(tasks => {
            console.log('Tasks fetched successfully:', tasks);
            
            // Process tasks into the DOM if there are any
            if (tasks && tasks.length > 0) {
                updateTasksList(tasks);
            } else {
                console.log('No tasks found for this project');
                // Empty the task list to show no data
                updateTasksList([]);
            }
        })
        .catch(error => {
            console.error('Error fetching tasks:', error);
        });
}

// Function to update the tasks list with fetched tasks
function updateTasksList(tasks) {
    console.log('Updating tasks list with', tasks.length, 'tasks');
    
    const taskList = document.getElementById('task-list');
    if (!taskList) {
        console.error('Task list element not found!');
        return;
    }
    
    // Clear existing tasks
    taskList.innerHTML = '';
    
    // Add fetched tasks to the list
    tasks.forEach(task => {
        const taskItem = createTaskListItem(task);
        taskList.appendChild(taskItem);
    });
    
    // Re-initialize tasks to ensure data attributes are set
    initializeTasks();
    
    // Show a message if no tasks were found
    if (tasks.length === 0) {
        const noTasksMessage = document.createElement('li');
        noTasksMessage.className = 'no-tasks-message';
        noTasksMessage.style.textAlign = 'center';
        noTasksMessage.style.padding = '20px';
        noTasksMessage.style.color = 'var(--text-secondary)';
        noTasksMessage.innerHTML = 'No tasks found. <a href="/tasks/new">Create a new task</a> to get started.';
        taskList.appendChild(noTasksMessage);
    }
    
    console.log('Task list updated successfully');
}

// Function to create a task list item
function createTaskListItem(task) {
    const li = document.createElement('li');
    li.className = 'task-item';
    li.id = `task-${task.id}`;
    
    // Set data attributes
    li.setAttribute('data-status', task.status?.toLowerCase() || 'todo');
    li.setAttribute('data-priority', task.priority?.toLowerCase() || 'medium');
    li.setAttribute('data-project', task.projectId || '1');
    
    li.innerHTML = `
        <div class="task-checkbox">
            <input type="checkbox" id="check-task-${task.id}" ${task.status === 'DONE' ? 'checked' : ''}>
        </div>
        <div class="task-content">
            <h4 class="task-title">${task.title}</h4>
            <p class="task-description">${task.description || ''}</p>
            <div class="task-meta">
                <span class="task-project">${task.projectName || 'Project'}</span>
                <span class="task-due">Due: ${task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'No date'}</span>
                <span class="task-priority priority-${task.priority?.toLowerCase() || 'medium'}" data-value="${task.priority?.toLowerCase() || 'medium'}">${capitalizeFirstLetter(task.priority?.toLowerCase() || 'medium')}</span>
            </div>
        </div>
        <div class="task-actions">
            <button class="btn btn-outline btn-sm">Edit</button>
        </div>
    `;
    
    return li;
}

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM content loaded, initializing task view');
    
    // Fetch tasks from API
    fetchTasks();
    
    // Initialize tasks
    initializeTasks();
    
    // Setup event listeners
    setupEventListeners();
    
    // Debug check for kanban button
    const kanbanViewBtn = document.getElementById('kanban-view');
    if (kanbanViewBtn) {
        console.log('Kanban view button found:', kanbanViewBtn);
    } else {
        console.error('Kanban view button not found!');
    }
});

function initializeTasks() {
    // Add data attributes to tasks for filtering
    const tasks = document.querySelectorAll('.task-item');
    
    tasks.forEach(task => {
        // Set Priority based on class
        const priorityEl = task.querySelector('.task-priority');
        if (priorityEl) {
            if (priorityEl.classList.contains('priority-high')) {
                task.setAttribute('data-priority', 'HIGH');
            } else if (priorityEl.classList.contains('priority-medium')) {
                task.setAttribute('data-priority', 'MEDIUM');
            } else if (priorityEl.classList.contains('priority-low')) {
                task.setAttribute('data-priority', 'LOW');
            } else if (priorityEl.classList.contains('priority-critical')) {
                task.setAttribute('data-priority', 'CRITICAL');
            }
        }
        
        // Get status from data attribute or checkbox state
        const checkbox = task.querySelector('.task-checkbox input');
        if (checkbox && checkbox.checked) {
            task.setAttribute('data-status', 'DONE');
        } else if (!task.getAttribute('data-status')) {
            // Default to 'TODO' if no status is set
            task.setAttribute('data-status', 'TODO');
        }
        
        // Convert status to uppercase to match enum values
        const status = task.getAttribute('data-status');
        if (status) {
            task.setAttribute('data-status', status.toUpperCase());
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
    // Task View Switching (List/Grid/Kanban)
    const listViewBtn = document.getElementById('list-view');
    const gridViewBtn = document.getElementById('grid-view');
    const kanbanViewBtn = document.getElementById('kanban-view');
    const taskList = document.getElementById('task-list');
    const kanbanBoard = document.getElementById('kanban-board');
    
    if (taskList && kanbanBoard) {
        // Function to switch to kanban view
        const switchToKanbanView = function() {
            console.log('Switching to kanban view');
            // Hide task list, show kanban
            taskList.style.display = 'none';
            kanbanBoard.style.display = 'flex';
            
            // Update view toggle buttons if they exist
            if (listViewBtn && gridViewBtn && kanbanViewBtn) {
                kanbanViewBtn.classList.add('active');
                listViewBtn.classList.remove('active');
                gridViewBtn.classList.remove('active');
            }
            
            // Log kanban board initialization
            console.log('Initializing kanban board...');
            initializeKanbanBoard();
            console.log('Kanban board initialized successfully');
            
            // Show notification
            showNotification('Switched to kanban view', 'info');
        };
        
        // List view button
        if (listViewBtn) {
            listViewBtn.addEventListener('click', function() {
                console.log('Switching to list view');
                // Show task list, hide kanban
                taskList.style.display = 'block';
                taskList.classList.remove('grid-view');
                kanbanBoard.style.display = 'none';
                
                // Update buttons
                listViewBtn.classList.add('active');
                gridViewBtn.classList.remove('active');
                kanbanViewBtn.classList.remove('active');
                
                // Show notification
                showNotification('Switched to list view', 'info');
            });
        }
        
        // Grid view button
        if (gridViewBtn) {
            gridViewBtn.addEventListener('click', function() {
                console.log('Switching to grid view');
                // Show task list in grid view, hide kanban
                taskList.style.display = 'block';
                taskList.classList.add('grid-view');
                kanbanBoard.style.display = 'none';
                
                // Update buttons
                gridViewBtn.classList.add('active');
                listViewBtn.classList.remove('active');
                kanbanViewBtn.classList.remove('active');
                
                // Show notification
                showNotification('Switched to grid view', 'info');
            });
        }
        
        // Kanban view from toggle buttons
        if (kanbanViewBtn) {
            console.log('Adding event listener to kanban view button');
            kanbanViewBtn.addEventListener('click', function() {
                console.log('Kanban view button clicked!');
                switchToKanbanView();
            });
        }
    }
    
    // Task Filtering
    const filterBtn = document.querySelector('.card-actions .btn-outline:first-child');
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
    
    // Task Edit Buttons - make sure this is applied to ALL buttons
    document.querySelectorAll('.task-actions .btn').forEach(btn => {
        // Remove any existing event listeners first
        const newBtn = btn.cloneNode(true);
        btn.parentNode.replaceChild(newBtn, btn);
        
        // Add the click event listener
        newBtn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
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
    
    // Count todo tasks
    const todoTasks = Array.from(allTasks).filter(task => 
        task.getAttribute('data-status') === 'TODO').length;
    
    const todoElement = document.querySelector('.stat-value:nth-of-type(2)');
    if (todoElement) {
        todoElement.textContent = todoTasks;
    }
    
    // Count in-progress
    const inProgressTasks = Array.from(allTasks).filter(task => 
        task.getAttribute('data-status') === 'IN_PROGRESS').length;
    
    const inProgressElement = document.querySelector('.stat-value:nth-of-type(3)');
    if (inProgressElement) {
        inProgressElement.textContent = inProgressTasks;
    }
    
    // Count done
    const doneTasks = Array.from(allTasks).filter(task => 
        task.getAttribute('data-status') === 'DONE').length;
    
    const doneElement = document.querySelector('.stat-value:nth-of-type(4)');
    if (doneElement) {
        doneElement.textContent = doneTasks;
    }
    
    // Count backlog
    const backlogTasks = Array.from(allTasks).filter(task => 
        task.getAttribute('data-status') === 'BACKLOG').length;
    
    const backlogElement = document.querySelector('.stat-value:nth-of-type(5)');
    if (backlogElement) {
        backlogElement.textContent = backlogTasks;
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
            
            // If kanban view is active, update the kanban board
            const kanbanViewBtn = document.getElementById('kanban-view');
            if (kanbanViewBtn && kanbanViewBtn.classList.contains('active')) {
                initializeKanbanBoard();
            }
        });
    }
}

function openEditModal(taskItem) {
    const modal = document.getElementById('edit-task-modal');
    if (!modal) {
        return;
    }
    
    // Extract task data
    const taskId = taskItem.id;
    const title = taskItem.querySelector('.task-title').textContent;
    const description = taskItem.querySelector('.task-description')?.textContent || '';
    const priority = taskItem.getAttribute('data-priority') || 'medium';
    const project = taskItem.getAttribute('data-project') || '1';
    const status = taskItem.getAttribute('data-status') || 'open';
    
    // Populate the form
    document.getElementById('edit-task-id').value = taskId;
    document.getElementById('edit-task-title').value = title;
    document.getElementById('edit-task-description').value = description;
    
    // Set the select elements
    const prioritySelect = document.getElementById('edit-task-priority');
    const projectSelect = document.getElementById('edit-task-project');
    const statusSelect = document.getElementById('edit-task-status');
    
    if (prioritySelect) prioritySelect.value = priority;
    if (projectSelect) projectSelect.value = project;
    if (statusSelect) statusSelect.value = status;
    
    // Display the modal with delay to ensure DOM is ready
    setTimeout(() => {
        modal.style.display = 'block';
    }, 50);
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

// Kanban Board Functions
function initializeKanbanBoard() {
    console.log('%c📋 Starting Kanban board initialization', 'color: blue; font-weight: bold');
    
    // Clear existing tasks from kanban columns
    console.log('Clearing existing kanban tasks');
    document.querySelectorAll('.kanban-tasks').forEach(column => {
        column.innerHTML = '';
    });
    
    // Reset task counts
    console.log('Resetting task counts');
    document.querySelectorAll('.kanban-column .task-count').forEach(count => {
        count.textContent = '0';
    });
    
    // Check if we should fetch tasks directly for Kanban board
    const urlParams = new URLSearchParams(window.location.search);
    const projectId = urlParams.get('project') || '1';  // Default to project ID 1
    console.log(`Loading tasks for project ID: ${projectId}`);
    
    // Always fetch tasks from DB for Kanban view for consistency
    console.log('%c📥 Fetching all tasks from database for Kanban view', 'color: green');
    
    // Fetch tasks specifically for Kanban
    console.log(`Making Kanban API request to: /api/tasks?projectId=${projectId}`);
    fetch(`/api/tasks?projectId=${projectId}`)
        .then(response => response.json())
        .then(apiTasks => {
            console.log(`%c✅ Fetched ${apiTasks.length} tasks from database for Kanban view:`, 'color: green; font-weight: bold');
            console.log(apiTasks);
            
            if (apiTasks.length === 0) {
                console.log('No tasks found in database. Kanban will be empty.');
            } else {
                // Use tasks from the database
                populateKanbanWithTasks(apiTasks);
            }
        })
        .catch(error => {
            console.error('Error fetching tasks for Kanban:', error);
        });
}

function populateKanbanWithTasks(apiTasks) {
    console.log('%c🧩 Organizing tasks into Kanban columns', 'color: purple; font-weight: bold');
    
    // Group tasks by status
    const tasksByStatus = {
        'todo': [],
        'in-progress': [],
        'done': [],
        'backlog': []
    };
    
    // Process API tasks
    apiTasks.forEach(task => {
        console.log(`Processing task: "${task.title}" (ID: ${task.id}, Status: ${task.status || 'undefined'})`);
        
        // Map API status to kanban status
        let kanbanStatus = task.status?.toLowerCase() || 'todo';
        
        // Status mapping to handle different status values
        if (kanbanStatus === 'done') {
            kanbanStatus = 'done';
        } else if (kanbanStatus === 'open') {
            // Map "open" status to "todo" column
            kanbanStatus = 'todo';
        } else if (kanbanStatus === 'in_progress' || kanbanStatus === 'in progress') {
            kanbanStatus = 'in-progress';
        }
        
        console.log(`🔄 Task "${task.title}": Mapped status "${task.status}" to kanban column "${kanbanStatus}"`);
        
        // Create task object for kanban
        const kanbanTask = {
            taskId: task.id.startsWith('task-') ? task.id : `task-${task.id}`,
            title: task.title,
            description: task.description || '',
            priority: task.priority?.toLowerCase() || 'medium',
            project: task.projectName || 'Project'
        };
        
        // Add task to appropriate group
        if (tasksByStatus[kanbanStatus]) {
            tasksByStatus[kanbanStatus].push(kanbanTask);
            console.log(`✅ Added task "${task.title}" to "${kanbanStatus}" column`);
        } else {
            // Default to todo if status doesn't match a column
            tasksByStatus['todo'].push(kanbanTask);
            console.log(`⚠️ Unknown status "${task.status}" - Defaulted task "${task.title}" to "todo" column`);
        }
    });
    
    // Render Kanban columns with tasks
    renderKanbanTasks(tasksByStatus);
}

// This function is no longer used since we only load from database
function processTasksForKanban(domTasks) {
    console.log('processTasksForKanban is deprecated - only loading from database now');
}

// Function to render tasks in kanban columns
function renderKanbanTasks(tasksByStatus) {
    console.log('%c🎨 Rendering tasks in Kanban columns', 'color: orange; font-weight: bold');
    
    // Render tasks in each column
    for (const [status, statusTasks] of Object.entries(tasksByStatus)) {
        const columnEl = document.getElementById(`kanban-${status}`);
        if (columnEl) {
            // Update task count
            const countEl = columnEl.closest('.kanban-column').querySelector('.task-count');
            if (countEl) {
                countEl.textContent = statusTasks.length;
                console.log(`📊 ${status} column: ${statusTasks.length} tasks`);
            }
            
            // Render tasks
            statusTasks.forEach(task => {
                const taskCard = createKanbanCard(task, status);
                columnEl.appendChild(taskCard);
                console.log(`📌 Rendered task "${task.title}" in ${status} column`);
            });
        } else {
            console.error(`❌ Could not find column element for status: ${status}`);
        }
    }
    
    // Setup drag and drop (for future implementation)
    setupKanbanDragAndDrop();
    
    // Summary of tasks by status
    const statusSummary = {
        'todo': tasksByStatus['todo'].length,
        'in-progress': tasksByStatus['in-progress'].length,
        'done': tasksByStatus['done'].length,
        'backlog': tasksByStatus['backlog'].length
    };
    
    console.log('%c✨ Kanban board initialization complete', 'color: green; font-weight: bold');
    console.log('%c📊 Task counts by status:', 'color: blue', statusSummary);
    
    // Log empty columns as a hint
    const emptyColumns = Object.entries(statusSummary)
        .filter(([_, count]) => count === 0)
        .map(([status, _]) => status);
    
    if (emptyColumns.length > 0) {
        console.log(`ℹ️ The following columns are empty: ${emptyColumns.join(', ')}`);
    }
}

function createKanbanCard(task, status) {
    // Create card element
    const card = document.createElement('div');
    card.className = 'kanban-card';
    card.setAttribute('data-task-id', task.taskId);
    card.setAttribute('data-status', status);
    
    // Create card content
    const cardContent = `
        <h4>${task.title}</h4>
        <div class="kanban-card-description">${task.description}</div>
        <div class="kanban-card-meta">
            <span class="kanban-project">${task.project}</span>
            <span class="kanban-priority ${task.priority}">${capitalizeFirstLetter(task.priority)}</span>
        </div>
    `;
    
    card.innerHTML = cardContent;
    
    // Add click handler to open task details
    card.addEventListener('click', function() {
        const originalTask = document.getElementById(task.taskId);
        if (originalTask) {
            openEditModal(originalTask);
        }
    });
    
    return card;
}

function setupKanbanDragAndDrop() {
    // This function can be expanded in the future to implement drag and drop
    // between kanban columns. For now, it's a placeholder.
}