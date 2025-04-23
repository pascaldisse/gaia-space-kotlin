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
  
  // Check if we're on the gaiascript page
  if (window.location.pathname === '/gaiascript') {
    setupGaiaScriptEditor();
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
              if (typeof openEditModal === 'function') {
                const taskItem = this.closest('.task-item');
                openEditModal(taskItem);
              }
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

function setupGaiaScriptEditor() {
    const editorContainer = document.getElementById('gaiascript-editor');
    const outputContainer = document.getElementById('kotlin-output');
    const compileButton = document.getElementById('compile-button');
    const statusMessage = document.getElementById('status-message');
    const appNameInput = document.getElementById('app-name');
    const loadSampleButton = document.getElementById('load-sample');
    
    if (!editorContainer || !outputContainer || !compileButton) {
        console.error('Missing required elements for GaiaScript editor');
        return;
    }
    
    // Load sample code
    loadSampleButton.addEventListener('click', () => {
        fetch('/api/gaiascript/sample')
            .then(response => response.json())
            .then(data => {
                editorContainer.value = data.gaiaScript;
                appNameInput.value = data.appName;
            })
            .catch(error => {
                console.error('Error loading sample:', error);
                statusMessage.textContent = 'Error loading sample';
                statusMessage.className = 'text-danger';
            });
    });
    
    // Compile button handler
    compileButton.addEventListener('click', () => {
        const gaiaScript = editorContainer.value;
        const appName = appNameInput.value || 'GaiaApp';
        
        if (!gaiaScript) {
            statusMessage.textContent = 'Please enter some GaiaScript code';
            statusMessage.className = 'text-warning';
            return;
        }
        
        statusMessage.textContent = 'Compiling...';
        statusMessage.className = 'text-info';
        
        // Call the API to compile
        fetch('/api/gaiascript/compile', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                gaiaScript: gaiaScript,
                appName: appName
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                outputContainer.textContent = data.kotlinCode;
                statusMessage.textContent = data.message;
                statusMessage.className = 'text-success';
                
                // Show compilation details
                if (data.compiledClasses && data.compiledClasses.length > 0) {
                    const compiledList = document.getElementById('compiled-classes');
                    compiledList.innerHTML = '';
                    
                    data.compiledClasses.forEach(className => {
                        const li = document.createElement('li');
                        li.textContent = className;
                        compiledList.appendChild(li);
                    });
                    
                    document.getElementById('compilation-details').style.display = 'block';
                }
            } else {
                outputContainer.textContent = data.kotlinCode || 'Compilation failed';
                statusMessage.textContent = data.message;
                statusMessage.className = 'text-danger';
            }
        })
        .catch(error => {
            console.error('Error compiling:', error);
            statusMessage.textContent = 'Error compiling: ' + error.message;
            statusMessage.className = 'text-danger';
        });
    });
}