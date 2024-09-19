<template>
    <div class="project-card">
        <div @click="goToDetailView">
            <h3>{{ project.project_name }}</h3>
            <p>{{ project.project_description }}</p>
            <p>Tasks: {{ project.project_tasks.length }}</p>
        </div>
        <button @click.stop="confirmDelete" class="delete-button">Delete</button>
    </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';
import { useRouter } from 'vue-router';
import { Project } from '@/types/Project';

export default defineComponent({
    name: 'ProjectCard',
    props: {
        project: {
            type: Object as PropType<Project>,
            required: true,
        },
    },
    emits: ['delete-project'],
    setup(props, { emit }) {
        const router = useRouter();

        const goToDetailView = () => {
            router.push({ name: 'ProjectDetail', params: { projectId: props.project.project_id.toString() } });
        };

        const confirmDelete = () => {
            if (confirm(`Are you sure you want to delete the project "${props.project.project_name}"?`)) {
                emit('delete-project', props.project.project_id);
            }
        };

        return {
            goToDetailView,
            confirmDelete,
        };
    },
});
</script>

<style scoped>
.project-card {
    border: 1px solid #ccc;
    padding: 15px;
    margin: 10px;
    border-radius: 5px;
    width: 200px;
    text-align: center;
    box-shadow: 2px 2px 12px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    position: relative;
}

.project-card:hover {
    background-color: #f7f7f7;
}

.delete-button {
    position: absolute;
    top: 5px;
    right: 5px;
    background-color: #e74c3c;
    color: white;
    border: none;
    border-radius: 3px;
    padding: 5px 10px;
    cursor: pointer;
}

.delete-button:hover {
    background-color: #c0392b;
}
</style>
