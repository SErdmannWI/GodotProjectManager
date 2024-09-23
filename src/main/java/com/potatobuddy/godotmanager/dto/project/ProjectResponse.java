package com.potatobuddy.godotmanager.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.potatobuddy.godotmanager.model.Project;
import com.potatobuddy.godotmanager.model.Task;

import java.util.List;

public class ProjectResponse {
    @JsonProperty("project_id")
    private String id;
    @JsonProperty("project_name")
    private String name;
    @JsonProperty("project_description")
    private String description;
    @JsonProperty("project_tasks")
    private List<Task> tasks;

    public ProjectResponse() {}

    public ProjectResponse(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.tasks = builder.tasks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }


    public static class Builder{
        private String id;
        private String name;
        private String description;
        private List<Task> tasks;
        private List<Task> backlog;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withTasks(List<Task> tasks) {
            this.tasks = tasks;
            return this;
        }

        public Builder withBacklog(List<Task> backlog){
            this.backlog = backlog;
            return this;
        }

        public ProjectResponse build() {
            return new ProjectResponse(this);
        }
    }

}
