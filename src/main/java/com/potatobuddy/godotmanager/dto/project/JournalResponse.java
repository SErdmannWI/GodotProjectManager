package com.potatobuddy.godotmanager.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class JournalResponse {
    @JsonProperty("entry_id")
    @NotNull
    private String id;
    @JsonProperty("entry_date")
    @NotNull
    private String date;
    @JsonProperty("entry_body")
    @NotNull
    private String body;

    public JournalResponse() {}

    public JournalResponse(Builder builder) {
        this.id = builder.id;
        this.date = builder.date;
        this.body = builder.body;
    }

    public @NotNull String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

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

    public static class Builder {
        private String id;
        private String date;
        private String body;


        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withDate(String date) {
            this.date = date;
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public JournalResponse build() {
            return new JournalResponse(this);
        }
    }
}
