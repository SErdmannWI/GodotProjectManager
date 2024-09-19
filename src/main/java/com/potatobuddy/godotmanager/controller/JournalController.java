package com.potatobuddy.godotmanager.controller;

import com.potatobuddy.godotmanager.dto.project.EditJournalEntryRequest;
import com.potatobuddy.godotmanager.dto.project.JournalResponse;
import com.potatobuddy.godotmanager.dto.project.NewJournalEntryRequest;
import com.potatobuddy.godotmanager.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    private JournalService journalService;

    @GetMapping("/all")
    public ResponseEntity<List<JournalResponse>> getAllJournalEntries() {
        List<JournalResponse> journalEntries = journalService.getAllJournalEntries();
        return ResponseEntity.ok(journalEntries);
    }

    @PostMapping("/newJournalEntry")
    public ResponseEntity<JournalResponse> createJournalEntry(@RequestBody NewJournalEntryRequest newJournalEntryRequest) {
        return ResponseEntity.ok(journalService.createNewJournalEntry(newJournalEntryRequest));
    }

    @PutMapping("/updateJournalEntry/{id}")
    public ResponseEntity<JournalResponse> updateJournalEntry(@PathVariable String id, @RequestBody EditJournalEntryRequest editJournalEntryRequest) {
        JournalResponse journalEntry = journalService.editJournalEntry(editJournalEntryRequest);
        if (journalEntry != null) {
            return ResponseEntity.ok(journalEntry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
