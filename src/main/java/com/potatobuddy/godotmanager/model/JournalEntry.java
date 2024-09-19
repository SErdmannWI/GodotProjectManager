package com.potatobuddy.godotmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "journal_entry")
public class JournalEntry {

    @Id
    @Column(name = "journal_id")
    private String id;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    public JournalEntry() {}

    public JournalEntry(Builder builder) {
        this.id = builder.id;
        this.date = builder.date;
        this.body = builder.body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static class Builder{
        private String id;
        private LocalDate date;
        private String body;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public JournalEntry build() {
            return new JournalEntry(this);
        }
    }
}
