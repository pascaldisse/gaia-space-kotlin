// Task View Management JavaScript

document.addEventListener('DOMContentLoaded', function() {
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
        });
        
        gridViewBtn.addEventListener('click', function() {
            taskList.classList.add('grid-view');
            gridViewBtn.classList.add('active');
            listViewBtn.classList.remove('active');
            // Store preference in localStorage
            localStorage.setItem('taskView', 'grid');
        });
        
        // Check for saved preference
        const savedView = localStorage.getItem('taskView');
        if (savedView === 'grid') {
            taskList.classList.add('grid-view');
            gridViewBtn.classList.add('active');
            listViewBtn.classList.remove('active');
        }
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
        });
    }
    
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            // Get filter values
            const statusFilter = document.getElementById('status-filter').value;
            const priorityFilter = document.getElementById('priority-filter').value;
            const projectFilter = document.getElementById('project-filter').value;
            const assigneeFilter = document.getElementById('assignee-filter').value;
            
            // Apply filters to tasks
            const tasks = document.querySelectorAll('.task-item');
            tasks.forEach(task => {
                // In a real app, you would check the task's data attributes
                // For demo, just randomly show/hide tasks
                const randomVisibility = Math.random() > 0.3;
                task.style.display = randomVisibility ? 'flex' : 'none';
                
                // If in grid view, we need to use 'block' instead of 'flex'
                if (taskList.classList.contains('grid-view') && randomVisibility) {
                    task.style.display = 'block';
                }
            });
            
            // Hide filters panel
            taskFilters.classList.remove('active');
            filterBtn.textContent = 'Filter';
            
            // Show filter applied message
            showFilterNotification();
        });
    }
    
    // Sort Tasks
    const sortSelect = document.getElementById('sort-tasks');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            const sortValue = this.value;
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
                    const priorityA = getPriorityValue(a.querySelector('.task-priority'));
                    const priorityB = getPriorityValue(b.querySelector('.task-priority'));
                    return priorityB - priorityA;
                } else if (sortValue === 'priority-asc') {
                    const priorityA = getPriorityValue(a.querySelector('.task-priority'));
                    const priorityB = getPriorityValue(b.querySelector('.task-priority'));
                    return priorityA - priorityB;
                } else if (sortValue === 'due-date-asc' || sortValue === 'due-date-desc') {
                    // For demo, just keep order or reverse
                    return sortValue === 'due-date-desc' ? -1 : 1;
                }
                return 0;
            });
            
            // Re-append sorted tasks
            const taskListEl = document.getElementById('task-list');
            tasks.forEach(task => {
                taskListEl.appendChild(task);
            });
        });
    }
    
    // Task Checkbox functionality
    const taskCheckboxes = document.querySelectorAll('.task-checkbox input');
    taskCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const taskItem = this.closest('.task-item');
            if (this.checked) {
                taskItem.classList.add('completed');
                // In a real app, you would send an API request to update the task status
                console.log('Task marked as completed:', taskItem.querySelector('.task-title').textContent);
            } else {
                taskItem.classList.remove('completed');
                console.log('Task marked as incomplete:', taskItem.querySelector('.task-title').textContent);
            }
        });
    });
    
    // Task Actions (Edit buttons)
    const editBtns = document.querySelectorAll('.task-actions .btn');
    editBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const taskItem = this.closest('.task-item');
            const taskTitle = taskItem.querySelector('.task-title').textContent;
            alert(`Editing task: ${taskTitle}`);
            // In a real app, you would open a modal or navigate to the edit page
        });
    });
});

// Helper Functions
function getPriorityValue(priorityEl) {
    if (!priorityEl) return 1;
    if (priorityEl.classList.contains('priority-high')) return 3;
    if (priorityEl.classList.contains('priority-medium')) return 2;
    return 1;
}

function showFilterNotification() {
    const notification = document.createElement('div');
    notification.className = 'notification success';
    notification.textContent = 'Filters applied successfully!';
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