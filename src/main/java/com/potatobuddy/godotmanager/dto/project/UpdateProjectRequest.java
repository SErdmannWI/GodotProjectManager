package com.potatobuddy.godotmanager.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.potatobuddy.godotmanager.model.Task;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UpdateProjectRequest {
    @JsonProperty("project_id")
    private String id;
    @JsonProperty("project_name")
    @NotNull(message = "Project name cannot be null")
    private String name;
    @JsonProperty("project_description")
    @NotNull(message = "Project description cannot be null")
    private String description;
    @JsonProperty("project_tasks")
    private List<Task> tasks;

    public @NotNull(message = "Project name cannot be null") String getName() {
        return name;
    }

    public void setName(@NotNull(message = "Project name cannot be null") String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(@NotNull(message = "Project id cannot be null") String id) {
        this.id = id;
    }

    public @NotNull(message = "Project description cannot be null") String getDescription() {
        return description;
    }

    public void setDescription(@NotNull(message = "Project description cannot be null") String description) {
        this.description = description;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "UpdateProjectRequest{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", tasks=" + tasks +
            '}';
    }
}
