<template>
  <div class="daily-journal">
    <h1>Daily Work Journal</h1>
    <p>Today's Date: {{ todayDate }}</p>
    
    <div class="new-entry-section">
      <label for="entry-date">Make Journal Entry For:</label>
      <input type="date" id="entry-date" v-model="selectedDate" :max="todayDate" />
      <button @click="createNewEntry" :disabled="isEditing">Create New Journal Entry</button>
    </div>
    
    <div v-if="isEditing" class="journal-entry-form">
      <textarea v-model="currentEntry.content" rows="10" cols="50"></textarea>
      <button @click="saveEntry">Save Entry</button>
      <button @click="cancelEdit">Cancel</button>
    </div>

    <div class="journal-entries">
      <h2>Past Entries</h2>
      <ul>
        <li v-for="entry in journalEntries" :key="entry.id">
          <div class="entry-header">
            <InlineEditPopover
              :value="formatDate(entry.date)"
              :onSave="(value: string) => updateEntryDate(entry, value)"
              type="date"
              :maxLength="10"
            />
            <button @click="editEntry(entry)">Edit</button>
          </div>
          <p>{{ entry.content }}</p>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from 'vue';
import { journalApi } from '@/api/journalApi';
import { format } from 'date-fns';
import InlineEditPopover from './InlineEditPopover.vue';

interface JournalEntry {
  id: string;
  date: string;
  content: string;
}

export default defineComponent({
  name: 'DailyJournal',
  components: {
    InlineEditPopover,
  },
  setup() {
    const journalEntries = ref<JournalEntry[]>([]);
    const isEditing = ref(false);
    const currentEntry = ref<JournalEntry>({ id: '', date: '', content: '' });
    const selectedDate = ref('');

    const todayDate = computed(() => {
      return format(new Date(), 'yyyy-MM-dd');
    });

    const fetchEntries = async () => {
      try {
        const response = await journalApi.getAllEntries();
        journalEntries.value = response.data.map((entry: any) => ({
          id: entry.entry_id,
          date: entry.entry_date,
          content: entry.entry_body
        }));
        journalEntries.value.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
      } catch (error) {
        console.error('Error fetching journal entries:', error);
      }
    };

    const createNewEntry = () => {
      currentEntry.value = { id: '', date: selectedDate.value || todayDate.value, content: '' };
      isEditing.value = true;
    };

    const saveEntry = async () => {
      try {
        let response;
        if (currentEntry.value.id) {
          response = await journalApi.updateEntry(currentEntry.value.id, {
            date: currentEntry.value.date,
            content: currentEntry.value.content
          });
        } else {
          response = await journalApi.createEntry({
            date: currentEntry.value.date,
            content: currentEntry.value.content
          });
        }
        
        const savedEntry = {
          id: response.data.entry_id,
          date: response.data.entry_date,
          content: response.data.entry_body
        };

        const existingEntryIndex = journalEntries.value.findIndex(entry => entry.id === savedEntry.id);
        if (existingEntryIndex !== -1) {
          journalEntries.value[existingEntryIndex] = savedEntry;
        } else {
          journalEntries.value.push(savedEntry);
        }
        
        journalEntries.value.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
        isEditing.value = false;
        selectedDate.value = '';
      } catch (error) {
        console.error('Error saving journal entry:', error);
      }
    };

    const cancelEdit = () => {
      isEditing.value = false;
      selectedDate.value = '';
    };

    const editEntry = (entry: JournalEntry) => {
      currentEntry.value = { ...entry };
      isEditing.value = true;
    };

    const updateEntryDate = async (entry: JournalEntry, newDate: string) => {
      if (newDate.length > 10) {
        console.error('Date is too long');
        return;
      }
      
      try {
        const response = await journalApi.updateEntry(entry.id, {
          date: newDate,
          content: entry.content
        });
        const updatedEntry = {
          id: response.data.entry_id,
          date: response.data.entry_date,
          content: response.data.entry_body
        };
        const entryIndex = journalEntries.value.findIndex(e => e.id === entry.id);
        if (entryIndex !== -1) {
          journalEntries.value[entryIndex] = updatedEntry;
        }
        journalEntries.value.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
      } catch (error) {
        console.error('Error updating entry date:', error);
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

    onMounted(fetchEntries);

    return {
      journalEntries,
      isEditing,
      currentEntry,
      selectedDate,
      todayDate,
      createNewEntry,
      saveEntry,
      cancelEdit,
      editEntry,
      updateEntryDate,
      formatDate,
    };
  },
});
</script>

<style scoped>
.daily-journal {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h1, h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

button {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 10px 15px;
  margin: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #2980b9;
}

.journal-entry-form {
  margin-top: 20px;
}

textarea {
  width: 100%;
  margin-bottom: 10px;
}

.journal-entries {
  margin-top: 30px;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}
</style>
