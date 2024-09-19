import api from './config';


export const journalApi = {
  getAllEntries: () => api.get(`/journal/all`),
  createEntry: (entry: { date: string; content: string }) => 
    api.post(`/journal/newJournalEntry`, {
      entry_date: entry.date,
      entry_body: entry.content
    }),
  updateEntry: (id: string, entry: { date: string; content: string }) => 
    api.put(`/journal/updateJournalEntry/${id}`, {
      entry_id: id,
      entry_date: entry.date,
      entry_body: entry.content
    })
};