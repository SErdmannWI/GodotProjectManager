<template>
  <div class="project-detail">
    <div v-if="project">
      <h1>{{ project.project_name }}</h1>
      <p>{{ project.project_description }}</p>

      <!-- View Mode Toggle -->
      <div class="view-mode-toggle">
        <button @click="viewMode = 'active'" :class="{ active: viewMode === 'active' }">Active Tasks</button>
        <button @click="viewMode = 'backlog'" :class="{ active: viewMode === 'backlog' }">Backlog</button>
      </div>

      <h2>{{ viewMode === 'active' ? 'Active Tasks' : 'Backlog' }}</h2>

      <add-task-form v-if="taskBeingEdited" :task="taskBeingEdited" @save-task="saveTaskEdit" class="add-task-form" />
      <add-task-form v-else @add-task="addTaskToProject" class="add-task-form" />

      <ul class="task-list">
        <li v-for="(task, index) in sortedTasks" :key="task.id || index" class="task-item">
          <div class="task-header">
            <button @click="confirmDeleteTask(task)" class="delete-task-button">Delete Task</button>
          </div>
          <div class="task-content">
            <InlineEditPopover
              :value="task.name"
              :onSave="(value: string) => updateTaskProperty(task, 'name', value)"
              :maxLength="255"
            />
            <InlineEditPopover
              :value="task.description"
              :onSave="(value: string) => updateTaskProperty(task, 'description', value)"
              type="textarea"
              :maxLength="255"
            />
            <InlineEditPopover
              :value="formatDate(task.dueDate)"
              :onSave="(value: string) => updateTaskProperty(task, 'dueDate', parseDate(value))"
              type="date"
              :maxLength="10"
            />
            <div class="task-status">
              <ColoredStatusSelect
                :modelValue="task.status"
                @update:modelValue="(value) => updateTaskProperty(task, 'status', value)"
                :options="statusOptions"
              />
              <font-awesome-icon :icon="getStatusIcon(task.status)" class="status-icon" />
            </div>
            <DifficultySelector
              :modelValue="task.difficulty"
              @update:modelValue="(value) => updateTaskProperty(task, 'difficulty', value)"
            />
            <div class="task-actions">
              <button @click="toggleTaskType(task)">
                {{ viewMode === 'active' ? 'Move to Backlog' : 'Move to Active' }}
              </button>
            </div>
          </div>

          <!-- Subtask Section -->
          <ul v-if="task.subtasks.length" class="subtask-list">
            <li v-for="subtask in task.subtasks.sort(sortByDueDate)" :key="subtask.id" class="subtask-item">
              <div class="subtask-content">
                <InlineEditPopover
                  :value="subtask.name"
                  :onSave="(value: string) => updateSubtaskProperty(task, subtask, 'name', value)"
                  :maxLength="255"
                />
                <InlineEditPopover
                  :value="subtask.description"
                  :onSave="(value: string) => updateSubtaskProperty(task, subtask, 'description', value)"
                  type="textarea"
                  :maxLength="255"
                />
                <InlineEditPopover
                  :value="formatDate(subtask.dueDate)"
                  :onSave="(value: string) => updateSubtaskProperty(task, subtask, 'dueDate', parseDate(value))"
                  type="date"
                  :maxLength="10"
                />
                <div class="subtask-status">
                  <ColoredStatusSelect
                    :modelValue="subtask.status"
                    @update:modelValue="(value) => updateSubtaskProperty(task, subtask, 'status', value)"
                    :options="statusOptions"
                  />
                  <font-awesome-icon :icon="getStatusIcon(subtask.status)" class="status-icon" />
                </div>
                <DifficultySelector
                  :modelValue="subtask.difficulty"
                  @update:modelValue="(value) => updateSubtaskProperty(task, subtask, 'difficulty', value)"
                />
                <div class="subtask-actions">
                  <button @click="confirmDeleteSubtask(task, subtask)" class="delete-button">Delete</button>
                </div>
              </div>
            </li>
          </ul>

          <!-- Subtask Form -->
          <div v-if="taskBeingEditedForSubtask && taskBeingEditedForSubtask.id === task.id" class="subtask-form">
            <input v-model="subtaskName" placeholder="Subtask Name" />
            <textarea v-model="subtaskDescription" placeholder="Description" rows="4"></textarea>
            <input v-model="subtaskDueDate" type="date" />
            <ColoredStatusSelect
              v-model="subtaskStatus"
              :options="statusOptions"
            />
            <DifficultySelector v-model="subtaskDifficulty" />
            <div class="button-group">
              <button @click="addSubtaskToTask(task)">Save</button>
              <button @click="cancelSubtaskForm" class="cancel-button">Cancel</button>
            </div>
          </div>

          <button v-if="!taskBeingEditedForSubtask" @click="showSubtaskForm(task)" class="add-subtask-button">Add Subtask</button>
        </li>
      </ul>
    </div>
    <p v-else>Loading project details...</p>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch } from 'vue';
import { projectApi } from '@/api/projectApi';
import { Project, Task, Subtask } from '@/types/Project';
import AddTaskForm from './AddTaskForm.vue';
import InlineEditPopover from './InlineEditPopover.vue';
import ColoredStatusSelect from './ColoredStatusSelect.vue';
import DifficultySelector from './DifficultySelector.vue';
import { debounce } from 'lodash';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { faCircle, faPencilAlt, faSpinner, faExclamationTriangle, faCheckCircle } from '@fortawesome/free-solid-svg-icons';

const statusOptions = [
  { value: 'Not Started', label: 'Not Started', color: '#808080' },
  { value: 'In Planning', label: 'In Planning', color: '#87CEFA' },
  { value: 'In Progress', label: 'In Progress', color: '#90EE90' },
  { value: 'Needs Attention', label: 'Needs Attention', color: '#FFA500' },
  { value: 'Finished', label: 'Finished', color: '#006400' },
];

export default defineComponent({
  name: 'ProjectDetailView',
  components: {
    AddTaskForm,
    InlineEditPopover,
    ColoredStatusSelect,
    DifficultySelector,
    FontAwesomeIcon,
  },
  props: {
    projectId: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const project = ref<Project | null>(null);
    const taskBeingEditedForSubtask = ref<Task | null>(null);
    const taskBeingEdited = ref<Task | null>(null);

    const viewMode = ref<'active' | 'backlog'>('active');

    const subtaskName = ref('');
    const subtaskDescription = ref('');
    const subtaskDueDate = ref('');
    const subtaskStatus = ref('Not Started');
    const subtaskDifficulty = ref('M');

    const fetchProject = async () => {
      try {
        const response = await projectApi.getProjectById(props.projectId);
        project.value = response.data;
        // Ensure project_tasks is initialized as an array
        project.value.project_tasks = project.value.project_tasks ?? [];

        // Initialize task_type and subtasks for each task
        project.value.project_tasks.forEach(task => {
          task.task_type = task.task_type ?? 'active';
          task.subtasks = task.subtasks ?? [];
        });
      } catch (error) {
        console.error('Error fetching project:', error);
      }
    };

    const sortByDueDate = (a: Task | Subtask, b: Task | Subtask) => {
      if (!a.dueDate) return 1;
      if (!b.dueDate) return -1;
      return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
    };

    const sortedTasks = computed(() => {
      if (!project.value) return [];
      const tasks = project.value.project_tasks ?? [];
      const filteredTasks = tasks.filter(task => task.task_type === viewMode.value);
      return [...filteredTasks].sort(sortByDueDate);
    });

    const debouncedUpdateProject = debounce(async () => {
      if (project.value) {
        const projectTasks = project.value.project_tasks ?? [];

      try {
        const response = await projectApi.updateProject(props.projectId, {
          project_id: project.value.project_id,
          project_name: project.value.project_name,
          project_description: project.value.project_description,
          project_tasks: projectTasks,
        });
      project.value = response.data;
      } catch (error) {
        console.error('Error updating project:', error);
      }
  }
}, 500);

    watch(() => project.value, debouncedUpdateProject, { deep: true });

    fetchProject();

    const addTaskToProject = async (task: Omit<Task, 'id'>) => {
      if (project.value) {
        console.log("Current view mode: ", viewMode.value);
        const newTask: Task = {
          ...task,
          id: '',
          subtasks: [],
          task_type: viewMode.value,
        };
        const taskList = project.value.project_tasks;
        if (!taskList.some(t => t.id === '')) {
          taskList.push(newTask);
          console.log("New task added: ", newTask);
        } else {
        console.error('Project is not initialized');
        }
      }
    };

    const confirmDeleteTask = (task: Task) => {
      if (confirm(`Are you sure you want to delete the task "${task.name}"?`)) {
        deleteTask(task);
      }
    };

    const deleteTask = async (task: Task) => {
      if (!project.value) return;
      const taskList = project.value.project_tasks;
      const index = taskList.findIndex(t => t.id === task.id);
      if (index !== -1) {
        taskList.splice(index, 1);
      }
    };

    const confirmDeleteSubtask = (task: Task, subtask: Subtask) => {
      if (confirm(`Are you sure you want to delete the subtask "${subtask.name}"?`)) {
        deleteSubtask(task, subtask);
      }
    };

    const deleteSubtask = (task: Task, subtask: Subtask) => {
      task.subtasks = task.subtasks.filter(s => s.id !== subtask.id);
    };

    const showSubtaskForm = (task: Task) => {
      taskBeingEditedForSubtask.value = task;
      subtaskName.value = '';
      subtaskDescription.value = '';
      subtaskDueDate.value = '';
      subtaskStatus.value = 'Not Started';
      subtaskDifficulty.value = 'M';
    };

    const cancelSubtaskForm = () => {
      taskBeingEditedForSubtask.value = null;
    };

    const addSubtaskToTask = async (task: Task) => {
      if (!taskBeingEditedForSubtask.value) return;
      if (!subtaskName.value.trim()) {
        alert('Subtask name is required');
        return;
      }
      const newSubtask: Subtask = {
        id: '',
        name: subtaskName.value.trim(),
        description: subtaskDescription.value,
        dueDate: subtaskDueDate.value,
        status: subtaskStatus.value,
        difficulty: subtaskDifficulty.value,
      };
      task.subtasks.push(newSubtask);
      taskBeingEditedForSubtask.value = null;
      subtaskName.value = '';
      subtaskDescription.value = '';
      subtaskDueDate.value = '';
      subtaskStatus.value = 'Not Started';
      subtaskDifficulty.value = 'M';
    };

    const updateTaskProperty = async (task: Task, property: keyof Task, value: string | number) => {
      if (!project.value) return;
      const tasks = project.value.project_tasks;
      const taskIndex = tasks.findIndex(t => t.id === task.id);
      if (taskIndex !== -1) {
        const taskToUpdate = tasks[taskIndex];
        if (property === 'dueDate') {
          taskToUpdate[property] = parseDate(value as string);
        } else {
          (taskToUpdate[property] as string | number) = value;
        }
      }
    };

    const updateSubtaskProperty = async (task: Task, subtask: Subtask, property: keyof Subtask, value: string | number) => {
      const subtaskIndex = task.subtasks.findIndex(s => s.id === subtask.id);
      if (subtaskIndex !== -1) {
        if (property === 'dueDate') {
          task.subtasks[subtaskIndex][property] = parseDate(value as string);
        } else {
          (task.subtasks[subtaskIndex][property] as string | number) = value;
        }
      }
    };

    const saveTaskEdit = (editedTask: Task) => {
      if (taskBeingEdited.value) {
        Object.assign(taskBeingEdited.value, editedTask);
        taskBeingEdited.value = null;
      }
    };

    const toggleTaskType = async (task: Task) => {
      if (!project.value) return;
      const tasks = project.value.project_tasks;
      const taskIndex = tasks.findIndex(t => t.id === task.id);
      if (taskIndex !== -1) {
        tasks[taskIndex].task_type = task.task_type === 'active' ? 'backlog' : 'active';
      }
    };

    const formatDate = (date: string) => {
      if (!date) return '';
      return date;
    };

    const parseDate = (dateString: string) => {
      if (!dateString) return '';
      return dateString;
    };

    const getStatusIcon = (status: string) => {
      switch (status) {
        case 'Not Started':
          return faCircle;
        case 'In Planning':
          return faPencilAlt;
        case 'In Progress':
          return faSpinner;
        case 'Needs Attention':
          return faExclamationTriangle;
        case 'Finished':
          return faCheckCircle;
        default:
          return faCircle;
      }
    };

    return {
      project,
      taskBeingEditedForSubtask,
      taskBeingEdited,
      subtaskName,
      subtaskDescription,
      subtaskDueDate,
      subtaskStatus,
      subtaskDifficulty,
      addTaskToProject,
      confirmDeleteTask,
      deleteTask,
      confirmDeleteSubtask,
      deleteSubtask,
      showSubtaskForm,
      cancelSubtaskForm,
      addSubtaskToTask,
      updateTaskProperty,
      updateSubtaskProperty,
      saveTaskEdit,
      toggleTaskType,
      statusOptions,
      formatDate,
      parseDate,
      getStatusIcon,
      sortedTasks,
      sortByDueDate,
      viewMode,
    };
  },
});
</script>

<style scoped>

h1 {
  color: #2c3e50;
  text-align: center;
  margin-bottom: 20px;
}

h2 {
  color: #34495e;
  border-bottom: 2px solid #3498db;
  text-align: center;
  padding-bottom: 10px;
  margin-top: 30px;
}
.project-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
  background-color: #f8f9fa;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.task-list {
  margin-top: 40px;
  list-style-type: none;
  padding-left: 0;
}

.task-item {
  position: relative;
  margin-top: 40px;
  padding: 20px;
  border: 2px solid #3498db;
  border-radius: 8px;
}

.task-header {
  position: absolute;
  top: -30px;
  right: 10px;
  z-index: 1;
}

.delete-task-button {
  padding: 5px 10px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.delete-task-button:hover {
  background-color: #c0392b;
}

.task-content {
  display: grid;
  grid-template-columns: 2fr 4fr 2fr 2fr 1fr 1fr;
  gap: 10px;
  align-items: center;
}

.subtask-list {
  margin-left: 20px;
  margin-top: 10px;
  list-style-type: none;
  padding-left: 0;
}

.subtask-item {
  margin-top: 10px;
  padding: 8px;
  border: 1px solid #2980b9;
  border-radius: 6px;
}

.subtask-content {
  display: grid;
  grid-template-columns: 2fr 4fr 2fr 2fr 1fr 1fr;
  gap: 10px;
  align-items: center;
}

.subtask-actions {
  display: flex;
  justify-content: flex-end;
}

.delete-button {
  padding: 5px 10px;
  background-color: #e74c3c;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.delete-button:hover {
  background-color: #c0392b;
}

.subtask-form {
  display: grid;
  grid-template-columns: 2fr 4fr 2fr 2fr 1fr 1fr;
  gap: 10px;
  align-items: start;
  margin-top: 10px;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #3498db;
  border-radius: 8px;
}

.subtask-form input,
.subtask-form select,
.subtask-form textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.subtask-form textarea {
  resize: vertical;
  min-height: 100px;
}

.button-group {
  display: flex;
  gap: 10px;
}

.cancel-button {
  background-color: #e74c3c;
}

.cancel-button:hover {
  background-color: #c0392b;
}

.add-subtask-button {
  margin-top: 10px;
}

.task-actions {
  display: flex;
  justify-content: flex-end;
  gap: 5px;
}

.task-actions button {
  padding: 5px 10px;
  background-color: #3498db;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.task-actions button:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.task-actions button:hover:not(:disabled) {
  background-color: #2980b9;
}

/* Adjust the first task to align with others */
.task-list > :first-child {
  margin-top: 40px;
}

/* Add this new rule to create more space between the Add Task form and the first task */
.add-task-form + .task-list {
  margin-top: 60px;
}

.editable-field {
  position: relative;
  transition: all 0.3s ease;
}

.editable-field:hover {
  background-color: #f0f0f0;
}

.status-icon {
  margin-left: 5px;
  font-size: 1em;
}

.task-status,
.subtask-status {
  display: flex;
  align-items: center;
}

/* Add styles for the view mode toggle */
.view-mode-toggle {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.view-mode-toggle button {
  padding: 10px 20px;
  margin: 0 10px;
  background-color: #3498db;
  color: rgb(255, 255, 255);
  border: none;
  cursor: pointer;
}

.view-mode-toggle button.active {
  background-color: #2980b9;
}
</style>
