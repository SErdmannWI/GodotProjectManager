<template>
    <div class="difficulty-selector">
      <button
        v-for="option in options"
        :key="option"
        class="difficulty-button"
        :class="{ active: modelValue === option }"
        @click="$emit('update:modelValue', option)"
      >
        {{ option }}
      </button>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, PropType } from 'vue';
  
  export default defineComponent({
    name: 'DifficultySelector',
    props: {
      modelValue: {
        type: String,
        required: true,
      },
      options: {
        type: Array as PropType<string[]>,
        default: () => ['XS', 'S', 'M', 'L', 'XL'],
      },
    },
    emits: ['update:modelValue'],
  });
  </script>
  
  <style scoped>
  .difficulty-selector {
    display: flex;
    gap: 5px;
  }
  
  .difficulty-button {
    padding: 5px 10px;
    border: 1px solid #ccc;
    background-color: #f0f0f0;
    cursor: pointer;
    transition: all 0.3s ease;
  }
  
  .difficulty-button.active {
    background-color: #3498db;
    color: white;
    border-color: #2980b9;
  }
  
  .difficulty-button:hover {
    background-color: #e0e0e0;
  }
  </style>