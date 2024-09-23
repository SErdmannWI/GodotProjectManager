<template>
  <div class="colored-status-select" @click="toggleDropdown" ref="selectContainer">
    <div class="selected-option">
      <span class="status-icon" :style="{ backgroundColor: selectedOption?.color }"></span>
      {{ selectedOption?.label }}
      <span class="dropdown-arrow">▼</span>
    </div>
    <div v-if="isOpen" class="options-dropdown">
      <div
        v-for="option in options"
        :key="option.value"
        class="option"
        @click="selectOption(option)"
      >
        <span class="status-icon" :style="{ backgroundColor: option.color }"></span>
        {{ option.label }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, computed, onMounted, onUnmounted } from 'vue';

interface StatusOption {
  value: string;
  label: string;
  color: string;
}

export default defineComponent({
  name: 'ColoredStatusSelect',
  props: {
    modelValue: String,
    options: Array as PropType<StatusOption[]>,
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const isOpen = ref(false);
    const selectContainer = ref<HTMLElement | null>(null);

    const selectedOption = computed(() => 
      props.options?.find(option => option.value === props.modelValue)
    );

    const toggleDropdown = () => {
      isOpen.value = !isOpen.value;
    };

    const selectOption = (option: StatusOption) => {
      emit('update:modelValue', option.value);
      isOpen.value = false;
    };

    const closeDropdown = (event: MouseEvent) => {
      if (selectContainer.value && !selectContainer.value.contains(event.target as Node)) {
        isOpen.value = false;
      }
    };

    onMounted(() => {
      document.addEventListener('click', closeDropdown);
    });

    onUnmounted(() => {
      document.removeEventListener('click', closeDropdown);
    });

    return {
      isOpen,
      selectedOption,
      toggleDropdown,
      selectOption,
      selectContainer,
    };
  },
});
</script>

<style scoped>
.colored-status-select {
  position: relative;
  width: 100%;
  cursor: pointer;
  font-size: 14px;
}

.selected-option {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #f8f8f8;
  transition: all 0.3s ease;
}

.selected-option:hover {
  background-color: #e8e8e8;
  border-color: #999;
}

.dropdown-arrow {
  margin-left: auto;
  font-size: 12px;
  color: #666;
}

.options-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  border: 1px solid #ccc;
  border-top: none;
  border-radius: 0 0 4px 4px;
  background-color: white;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.option {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  transition: background-color 0.2s ease;
}

.option:hover {
  background-color: #f0f0f0;
}

.status-icon {
  display: inline-block;
  width: 12px;
  height: 12px;
  margin-right: 8px;
  border-radius: 50%;
}
</style>
