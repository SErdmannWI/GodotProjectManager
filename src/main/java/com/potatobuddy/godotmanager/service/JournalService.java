package com.potatobuddy.godotmanager.service;

import com.potatobuddy.godotmanager.dto.project.EditJournalEntryRequest;
import com.potatobuddy.godotmanager.dto.project.JournalResponse;
import com.potatobuddy.godotmanager.dto.project.NewJournalEntryRequest;
import com.potatobuddy.godotmanager.exceptions.InvalidProjectRequestException;
import com.potatobuddy.godotmanager.model.JournalEntry;
import com.potatobuddy.godotmanager.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class JournalService {

    private JournalEntryRepository journalEntryRepository;

    @Autowired
    public JournalService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    public JournalResponse createNewJournalEntry(NewJournalEntryRequest newJournalEntryRequest) {
        if (newJournalEntryRequest.getDate() == null || newJournalEntryRequest.getDate().isEmpty()
            || newJournalEntryRequest.getDate().isBlank()) {
            throw new InvalidProjectRequestException("Journal entry date cannot be empty or null");
        } else if (newJournalEntryRequest.getBody() == null || newJournalEntryRequest.getBody().isEmpty()
            || newJournalEntryRequest.getBody().isBlank()) {
            throw new InvalidProjectRequestException("Journal entry body cannot be empty or null");
        }

        JournalEntry newJournalEntry = new JournalEntry.Builder()
            .withId(UUID.randomUUID().toString())
            .withDate(LocalDate.parse(newJournalEntryRequest.getDate(), DateTimeFormatter.ISO_LOCAL_DATE))
            .withBody(newJournalEntryRequest.getBody())
            .build();

        JournalEntry savedJournalEntry = journalEntryRepository.save(newJournalEntry);

        return new JournalResponse.Builder()
            .withId(savedJournalEntry.getId())
            .withDate(savedJournalEntry.getDate().toString())
            .withBody(savedJournalEntry.getBody())
            .build();
    }

    public JournalResponse editJournalEntry(EditJournalEntryRequest editJournalEntryRequest) {
        if (editJournalEntryRequest.getId() == null || editJournalEntryRequest.getId().isEmpty()
            || editJournalEntryRequest.getId().isBlank()) {
            throw new InvalidProjectRequestException("Journal entry ID cannot be empty or null");
        } else if (editJournalEntryRequest.getDate() == null || editJournalEntryRequest.getDate().isEmpty()
            || editJournalEntryRequest.getDate().isBlank()) {
            throw new InvalidProjectRequestException("Journal entry date cannot be empty or null");
        } else if (editJournalEntryRequest.getBody() == null || editJournalEntryRequest.getBody().isEmpty()
            || editJournalEntryRequest.getBody().isBlank()) {
            throw new InvalidProjectRequestException("Journal entry body cannot be empty or null");
        }

        JournalEntry journalEntry = new JournalEntry.Builder()
            .withId(editJournalEntryRequest.getId())
            .withDate(LocalDate.parse(editJournalEntryRequest.getDate(), DateTimeFormatter.ISO_LOCAL_DATE))
            .withBody(editJournalEntryRequest.getBody())
            .build();

        JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);

        return journalToJournalResponse(savedJournalEntry);
    }

    public List<JournalResponse> getAllJournalEntries() {
        return journalEntryRepository.findAll().stream()
            .map(JournalService::journalToJournalResponse)
            .toList();
    }

    private static JournalResponse journalToJournalResponse(JournalEntry journalEntry) {
        return new JournalResponse.Builder()
            .withId(journalEntry.getId())
            .withDate(journalEntry.getDate().toString())
            .withBody(journalEntry.getBody())
            .build();
    }
}
