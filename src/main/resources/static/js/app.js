// Main Application JavaScript

document.addEventListener('DOMContentLoaded', function() {
  // Initialize tooltips
  const tooltips = document.querySelectorAll('[data-toggle="tooltip"]');
  tooltips.forEach(tooltip => {
    tooltip.title = tooltip.getAttribute('data-title');
    tooltip.addEventListener('mouseenter', showTooltip);
    tooltip.addEventListener('mouseleave', hideTooltip);
  });

  // Task checkboxes
  const taskCheckboxes = document.querySelectorAll('.task-checkbox input');
  taskCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
      const taskItem = this.closest('.task-item');
      if (this.checked) {
        taskItem.classList.add('completed');
      } else {
        taskItem.classList.remove('completed');
      }
    });
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
      
      if (taskTitle.trim() !== '') {
        // Here you would normally send an API request
        console.log('Adding task:', { title: taskTitle, description: taskDescription, priority: taskPriority });
        
        // For demo purposes, just reset the form
        addTaskForm.reset();
        
        // Show a success message
        showNotification('Task added successfully!', 'success');
      }
    });
  }
});

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