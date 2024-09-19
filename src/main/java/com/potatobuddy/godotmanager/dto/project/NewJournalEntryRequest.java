package com.potatobuddy.godotmanager.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class NewJournalEntryRequest {
    @JsonProperty("entry_date")
    @NotNull
    private String date;
    @JsonProperty("entry_body")
    @NotNull
    private String body;

    public @NotNull String getDate() {
        return date;
    }

    public void setDate(@NotNull String date) {
        this.date = date;
    }

    public @NotNull String getBody() {
        return body;
    }

    public void setBody(@NotNull String body) {
        this.body = body;
    }
}
