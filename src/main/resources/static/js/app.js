// Main Application JavaScript

document.addEventListener('DOMContentLoaded', function() {
  // Initialize tooltips
  const tooltips = document.querySelectorAll('[data-toggle="tooltip"]');
  tooltips.forEach(tooltip => {
    tooltip.title = tooltip.getAttribute('data-title');
    tooltip.addEventListener('mouseenter', showTooltip);
    tooltip.addEventListener('mouseleave', hideTooltip);
  });

  // Mobile menu toggle
  const menuToggle = document.querySelector('.menu-toggle');
  const navLinks = document.querySelector('.nav-links');
  
  if (menuToggle) {
    menuToggle.addEventListener('click', function() {
      navLinks.classList.toggle('active');
    });
  }

  // Add task form submission
  const addTaskForm = document.getElementById('add-task-form');
  if (addTaskForm) {
    addTaskForm.addEventListener('submit', function(event) {
      event.preventDefault();
      const taskTitle = document.getElementById('task-title').value;
      const taskDescription = document.getElementById('task-description').value;
      const taskPriority = document.getElementById('task-priority').value;
      const projectId = document.getElementById('task-project') ? 
                        document.getElementById('task-project').value : '1';
      
      if (taskTitle.trim() !== '') {
        // Create a new task element
        const taskList = document.getElementById('task-list');
        if (taskList) {
          // Determine project name
          let projectName = 'Gaia Space Backend';
          if (projectId === '2') projectName = 'Gaia Space Frontend';
          if (projectId === '3') projectName = 'Documentation';
          
          // Generate unique ID
          const taskId = 'task-' + Date.now();
          
          // Create task HTML
          const taskHTML = `
            <li class="task-item" id="${taskId}" data-priority="${taskPriority}" data-status="open" data-project="${projectId}">
              <div class="task-checkbox">
                <input type="checkbox" id="check-${taskId}">
              </div>
              <div class="task-content">
                <h4 class="task-title">${taskTitle}</h4>
                <p class="task-description">${taskDescription || ''}</p>
                <div class="task-meta">
                  <span class="task-project">${projectName}</span>
                  <span class="task-priority priority-${taskPriority}" data-value="${taskPriority}">${capitalizeFirstLetter(taskPriority)}</span>
                </div>
              </div>
              <div class="task-actions">
                <button class="btn btn-outline btn-sm">Edit</button>
              </div>
            </li>
          `;
          
          // Add to list
          taskList.insertAdjacentHTML('afterbegin', taskHTML);
          
          // Attach event listener to new checkbox
          const newCheckbox = document.querySelector(`#check-${taskId}`);
          if (newCheckbox) {
            newCheckbox.addEventListener('change', function() {
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
              
              // Update counts
              if (typeof updateTaskCounts === 'function') {
                updateTaskCounts();
              }
            });
          }
          
          // Attach event listener to edit button
          const newEditBtn = document.querySelector(`#${taskId} .task-actions .btn`);
          if (newEditBtn) {
            newEditBtn.addEventListener('click', function() {
              alert(`Editing task: ${taskTitle}`);
            });
          }
          
          // Update task counts
          if (typeof updateTaskCounts === 'function') {
            updateTaskCounts();
          }
          
          // Reset form
          addTaskForm.reset();
          
          // Show notification
          showNotification('Task added successfully!', 'success');
        }
      }
    });
  }
});

function capitalizeFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

// Helper functions
function showTooltip(event) {
  const tooltip = event.target;
  const tooltipText = document.createElement('div');
  tooltipText.className = 'tooltip-text';
  tooltipText.textContent = tooltip.getAttribute('data-title');
  
  tooltip.appendChild(tooltipText);
  
  const rect = tooltip.getBoundingClientRect();
  tooltipText.style.top = rect.height + 'px';
  tooltipText.style.left = (rect.width / 2 - tooltipText.offsetWidth / 2) + 'px';
}

function hideTooltip(event) {
  const tooltip = event.target;
  const tooltipText = tooltip.querySelector('.tooltip-text');
  if (tooltipText) {
    tooltip.removeChild(tooltipText);
  }
}

function showNotification(message, type = 'info') {
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