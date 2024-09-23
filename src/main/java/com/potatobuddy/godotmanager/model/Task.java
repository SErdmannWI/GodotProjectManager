package com.potatobuddy.godotmanager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "task_id")
    private String id;
    @Column(name = "task_name")
    private String name;
    @Column(name = "task_description")
    private String description;
    @Column(name = "task_status")
    private String status;
    @Column(name = "task_due_date")
    private LocalDate dueDate;
    @Column(name = "task_difficulty")
    private String difficulty;
    @Column(name= "task_type")
    @JsonProperty("task_type")
    private String taskType;
    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Subtask> subtasks;

    public Task() {}

    public Task(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
        this.dueDate = builder.dueDate;
        this.difficulty = builder.difficulty;
        this.taskType = builder.taskType;
        this.project = builder.project;
        this.subtasks = builder.subtasks != null ? builder.subtasks : new ArrayList<>();
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        subtask.setTask(this);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = LocalDate.parse(dueDate, DateTimeFormatter.ISO_DATE);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    /**-------------------------------------------- Builder Class ----------------------------------------------------*/

    public static class Builder{
        private String id;
        private String name;
        private String description;
        private String status;
        private LocalDate dueDate;
        private String difficulty;
        private String taskType;
        private Project project;
        private List<Subtask> subtasks;

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

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder withDifficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Builder withTaskType(String taskType) {
            this.taskType = taskType;
            return this;
        }

        public Builder withProject(Project project) {
            this.project = project;
            return this;
        }

        public Builder withSubtasks(List<Subtask> subtasks) {
            this.subtasks = subtasks;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(status, task.status) && Objects.equals(dueDate, task.dueDate) && Objects.equals(difficulty, task.difficulty) && Objects.equals(project, task.project) && Objects.equals(subtasks, task.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, dueDate, difficulty, project, subtasks);
    }
}
