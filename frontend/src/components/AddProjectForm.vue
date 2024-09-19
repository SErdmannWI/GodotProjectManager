<template>
  <div class="add-project-form">
    <h3>{{ isEditMode ? 'Edit Project' : 'Add New Project' }}</h3>
    <form @submit.prevent="handleSubmit" class="add-project-form">
      <div class="form-group">
        <label for="projectName">Project Name:</label>
        <input
          id="projectName"
          v-model="projectName"
          required
          :maxlength="255"
        />
        <div class="char-count" :class="{ 'char-count-limit': isNameAtLimit }">
          {{ projectName.length }} / 255
        </div>
      </div>
      <div class="form-group">
        <label for="projectDescription">Description:</label>
        <textarea
          id="projectDescription"
          v-model="projectDescription"
          :maxlength="255"
        ></textarea>
        <div class="char-count" :class="{ 'char-count-limit': isDescriptionAtLimit }">
          {{ projectDescription.length }} / 255
        </div>
      </div>
      <button type="submit" :disabled="isNameAtLimit || isDescriptionAtLimit">
        {{ isEditMode ? 'Save Project' : 'Add Project' }}
      </button>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from 'vue';

export default defineComponent({
  name: 'AddProjectForm',
  props: {
    project: {
      type: Object,
      default: null,
    },
  },
  emits: ['add-project', 'save-project'],
  setup(props, { emit }) {
    const projectName = ref(props.project?.name || '');
    const projectDescription = ref(props.project?.description || '');

    const isNameAtLimit = computed(() => projectName.value.length >= 255);
    const isDescriptionAtLimit = computed(() => projectDescription.value.length >= 255);
    const isEditMode = computed(() => !!props.project);

    const handleSubmit = () => {
      const newProject = {
        project_name: projectName.value,
        project_description: projectDescription.value,
      };

      if (props.project) {
        emit('save-project', { ...props.project, ...newProject });
      } else {
        emit('add-project', newProject);
      }

      projectName.value = '';
      projectDescription.value = '';
    };

    return {
      projectName,
      projectDescription,
      isNameAtLimit,
      isDescriptionAtLimit,
      isEditMode,
      handleSubmit,
    };
  },
});
</script>

<style scoped>
.add-project-form {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input, textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

.char-count {
  font-size: 0.8em;
  color: #666;
  text-align: right;
}

.char-count-limit {
  color: red;
}
</style>
