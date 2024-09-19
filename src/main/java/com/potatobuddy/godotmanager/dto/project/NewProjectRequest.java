package com.potatobuddy.godotmanager.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class NewProjectRequest {
    @JsonProperty("project_name")
    @NotNull(message = "Project name cannot be null")
    private String name;
    @JsonProperty("project_description")
    @NotNull(message = "Project description cannot be null")
    private String description;

    public @NotNull(message = "Project description cannot be null") String getDescription() {
        return description;
    }

    public void setDescription(@NotNull(message = "Project description cannot be null") String description) {
        this.description = description;
    }

    public @NotNull(message = "Project name cannot be null") String getName() {
        return name;
    }

    public void setName(@NotNull(message = "Project name cannot be null") String name) {
        this.name = name;
    }
}
