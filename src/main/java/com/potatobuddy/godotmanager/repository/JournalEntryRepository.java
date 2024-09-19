package com.potatobuddy.godotmanager.repository;

import com.potatobuddy.godotmanager.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, String> {
}
