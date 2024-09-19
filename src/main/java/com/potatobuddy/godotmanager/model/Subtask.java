package com.potatobuddy.godotmanager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Table(name = "subtasks")
public class Subtask {

    @Id
    @Column(name = "subtask_id")
    private String id;
    @Column(name = "subtask_name")
    private String name;
    @Column(name = "subtask_description")
    private String description;
    @Column(name = "subtask_status")
    private String status;
    @Column(name = "subtask_due_date")
    private LocalDate dueDate;
    @Column(name = "subtask_difficulty")
    private String difficulty;

    @ManyToOne
    @JoinColumn(name = "task_id")
    @JsonBackReference
    private Task task;

    public Subtask() {}

    public Subtask(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
        this.dueDate = builder.dueDate;
        this.difficulty = builder.difficulty;
        this.task = builder.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
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
        LocalDate date = LocalDate.parse(dueDate, DateTimeFormatter.ISO_DATE);
        this.dueDate = date;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public static class Builder{
        private String id;
        private String name;
        private String description;
        private String status;
        private LocalDate dueDate;
        private String difficulty;
        private Task task;

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

        public Builder withTask(Task task) {
            this.task = task;
            return this;
        }

        public Subtask build() {
            return new Subtask(this);
        }
    }
}
