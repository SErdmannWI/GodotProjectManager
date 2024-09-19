<template>
    <nav class="navigation">
      <router-link to="/" class="nav-link">Home</router-link>
      <router-link to="/journal" class="nav-link">Daily Journal</router-link>
    </nav>
  </template>
  
  <script lang="ts">
  import { defineComponent } from 'vue';
  
  export default defineComponent({
    name: 'Navigation',
  });
  </script>
  
  <style scoped>
  .navigation {
    background-color: #34495e;
    padding: 15px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
  
  .nav-link {
    color: white;
    text-decoration: none;
    font-weight: bold;
    padding: 10px 0;
    font-size: 18px;
  }
  
  .nav-link:hover {
    color: #42b983;
  }
  
  .router-link-active {
    color: #42b983;
  }
  </style>