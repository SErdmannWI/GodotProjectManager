<template>
  <div class="add-task-form">
    <h3>{{ isEditMode ? 'Edit Task' : 'Add New Task' }}</h3>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="taskName">Task Name:</label>
        <input
          id="taskName"
          v-model="taskName"
          required
          :maxlength="255"
        />
        <div class="char-count" :class="{ 'char-count-limit': isNameAtLimit }">
          {{ taskName.length }} / 255
        </div>
      </div>
      <div class="form-group">
        <label for="taskDescription">Description:</label>
        <textarea
          id="taskDescription"
          v-model="taskDescription"
          :maxlength="255"
        ></textarea>
        <div class="char-count" :class="{ 'char-count-limit': isDescriptionAtLimit }">
          {{ taskDescription.length }} / 255
        </div>
      </div>
      <div class="form-group">
        <label for="taskDueDate">Due Date:</label>
        <input
          id="taskDueDate"
          v-model="taskDueDate"
          type="date"
        />
      </div>
      <div class="form-group">
        <label for="taskStatus">Status:</label>
        <ColoredStatusSelect
          v-model="taskStatus"
          :options="statusOptions"
        />
      </div>
      <div class="form-group">
        <label for="taskDifficulty">Difficulty:</label>
        <DifficultySelector
          v-model="taskDifficulty"
        />
      </div>
      <button type="submit" :disabled="isNameAtLimit || isDescriptionAtLimit">Add Task</button>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from 'vue';
import ColoredStatusSelect from './ColoredStatusSelect.vue';
import DifficultySelector from './DifficultySelector.vue';

const statusOptions = [
  { value: 'Not Started', label: 'Not Started', color: '#808080' },
  { value: 'In Planning', label: 'In Planning', color: '#87CEFA' },
  { value: 'In Progress', label: 'In Progress', color: '#90EE90' },
  { value: 'Needs Attention', label: 'Needs Attention', color: '#FFA500' },
  { value: 'Finished', label: 'Finished', color: '#006400' },
];

export default defineComponent({
  name: 'AddTaskForm',
  components: {
    ColoredStatusSelect,
    DifficultySelector,
  },
  props: {
    task: {
      type: Object,
      default: null,
    },
  },
  emits: ['add-task', 'save-task'],
  setup(props, { emit }) {
    const taskName = ref(props.task?.name || '');
    const taskDescription = ref(props.task?.description || '');
    const taskDueDate = ref(props.task?.dueDate || '');
    const taskStatus = ref(props.task?.status || 'Not Started');
    const taskDifficulty = ref(props.task?.difficulty || 'Medium');

    const isNameAtLimit = computed(() => taskName.value.length >= 255);
    const isDescriptionAtLimit = computed(() => taskDescription.value.length >= 255);

    const isEditMode = computed(() => !!props.task);

    const handleSubmit = () => {
      const newTask = {
        name: taskName.value,
        description: taskDescription.value,
        dueDate: taskDueDate.value,
        status: taskStatus.value,
        difficulty: taskDifficulty.value,
      };

      if (props.task) {
        emit('save-task', { ...props.task, ...newTask });
      } else {
        emit('add-task', newTask);
      }

      taskName.value = '';
      taskDescription.value = '';
      taskDueDate.value = '';
    };

    return {
      taskName,
      taskDescription,
      taskDueDate,
      taskStatus,
      taskDifficulty,
      isNameAtLimit,
      isDescriptionAtLimit,
      isEditMode,
      handleSubmit,
      statusOptions,
    };
  },
});
</script>

<style scoped>
.add-task-form {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

h3 {
  margin-top: 0;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

input, textarea, select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

button {
  background-color: #007bff;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #0056b3;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.char-count {
  font-size: 12px;
  color: #777;
  text-align: right;
  margin-top: 4px;
}

.char-count-limit {
  color: #dc3545;
}
</style>
