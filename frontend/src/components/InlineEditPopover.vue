<template>
    <div class="inline-edit-popover">
        <div v-if="!isEditing" class="display-value" @click="startEdit">
            {{ displayValue }}
            <span class="edit-icon">✎</span>
        </div>
        <div v-else class="edit-container">
            <input
                v-if="type !== 'textarea'"
                :type="type"
                v-model="editValue"
                @blur="saveEdit"
                @keyup.enter="saveEdit"
                :maxlength="maxLength"
            />
            <textarea
                v-else
                v-model="editValue"
                @blur="saveEdit"
                :maxlength="maxLength"
            ></textarea>
            <div v-if="showCharCount" class="char-count" :class="{ 'char-count-limit': isAtLimit }">
                {{ editValue.length }} / {{ maxLength }}
            </div>
            <button @click="saveEdit" :disabled="isDisabled">Save</button>
            <button @click="cancelEdit">Cancel</button>
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch } from 'vue';

export default defineComponent({
    props: {
        value: { type: String, required: true },
        onSave: { type: Function, required: true },
        type: { type: String, default: 'text' },
        maxLength: { type: Number, default: 255 },
    },
    setup(props, { emit }) {
        const isEditing = ref(false);
        const editValue = ref(props.value);

        const displayValue = computed(() => {
            if (props.type === 'date') {
                const [year, month, day] = props.value.split('-');
                return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
            }
            return props.value;
        });

        const showCharCount = computed(() => {
            return props.type !== 'date' && props.maxLength < 65535;
        });

        const isAtLimit = computed(() => {
            return editValue.value.length >= props.maxLength;
        });

        watch(() => props.value, (newValue) => {
            if (!isEditing.value) {
                editValue.value = newValue;
            }
        });

        const startEdit = () => {
            isEditing.value = true;
            editValue.value = props.value;
        };

        const saveEdit = () => {
            if (editValue.value !== props.value) {
                props.onSave(editValue.value);
            }
            isEditing.value = false;
        };

        const cancelEdit = () => {
            isEditing.value = false;
            editValue.value = props.value;
        };

        const isDisabled = computed(() => {
            if (props.type === 'date') {
                return false;
            }
            return isAtLimit.value;
        });

        return {
            isEditing,
            editValue,
            displayValue,
            showCharCount,
            isAtLimit,
            startEdit,
            saveEdit,
            cancelEdit,
            isDisabled,
        };
    },
});
</script>

<style scoped>
.char-count {
    font-size: 0.8em;
    color: #666;
    text-align: right;
    margin-top: 4px;
}

.char-count-limit {
    color: red;
    font-weight: bold;
}

.edit-container {
    display: flex;
    flex-direction: column;
}

input, textarea {
    margin-bottom: 5px;
}

button {
    margin-top: 5px;
}
</style>
