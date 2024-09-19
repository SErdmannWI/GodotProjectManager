<template>
    <div class="project-list">
      <h1>My Projects</h1>
      <div class="project-container">
        <ProjectCard
          v-for="project in projects"
          :key="project.project_id"
          :project="project"
          @delete-project="deleteProject"
        />
      </div>
      <AddProjectForm @add-project="addProject" />
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, onMounted } from 'vue';
  import { projectApi } from '@/api/projectApi';
  import { Project, NewProject } from '@/types/Project';
  import ProjectCard from './ProjectCard.vue';
  import AddProjectForm from './AddProjectForm.vue';

  export default defineComponent({
    components: {
      ProjectCard,
      AddProjectForm,
    },
    setup() {
      const projects = ref<Project[]>([]);

      const fetchProjects = async () => {
        try {
          const response = await projectApi.getAllProjects();
          projects.value = response.data;
        } catch (error) {
          console.error('Error fetching projects:', error);
        }
      };

      const deleteProject = async (projectId: string) => {
        try {
          await projectApi.deleteProject(projectId);
          projects.value = projects.value.filter(project => project.project_id !== projectId);
        } catch (error) {
          console.error('Error deleting project:', error);
        }
      };

      const addProject = async (newProject: NewProject) => {
        try {
          const response = await projectApi.createProject(newProject);
          projects.value.push(response.data);
        } catch (error) {
          console.error('Error adding project:', error);
        }
      };

      onMounted(fetchProjects);

      return { 
        projects,
        deleteProject,
        addProject
      };
    },
  });
  </script>
  
  <style scoped>
  .project-list {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }

  h1 {
    text-align: center;
    color: #2c3e50;
    margin-bottom: 30px;
  }

  .project-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
  }
  </style>
