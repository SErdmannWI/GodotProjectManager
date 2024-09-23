package com.potatobuddy.godotmanager.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @Column(name = "project_id")
    @JsonProperty("project_id")
    private String id;
    @Column(name = "name")
    @JsonProperty("project_name")
    private String name;
    @Column(name = "description")
    @JsonProperty("project_description")
    private String description;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @JsonProperty("project_tasks")
    private List<Task> tasks;

    public Project() {}

    public Project(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.tasks = builder.tasks != null ? builder.tasks : new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) && Objects.equals(name, project.name) && Objects.equals(description, project.description) && Objects.equals(tasks, project.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, tasks);
    }

    /**-------------------------------------------- Builder Class ----------------------------------------------------*/

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

        public Builder withBacklog(List<Task> backlog) {
            this.backlog = backlog;
            return this;
        }

        public Project build() {
            return new Project(this);
        }
    }
}
