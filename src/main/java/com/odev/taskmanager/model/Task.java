package com.odev.taskmanager.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String priority;
    private String userEmail;
    private LocalDateTime dueDate;

    // Constructor
    public Task(String title, String description, String priority, String userEmail, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.userEmail = userEmail;
        this.dueDate = dueDate;
    }
    public Task(String title, String description, String priority, String userEmail) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.userEmail = userEmail;
        this.dueDate = LocalDateTime.now(); // Varsayılan olarak şu anki tarih
    }

    // Getters - Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
