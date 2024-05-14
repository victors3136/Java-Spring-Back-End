package com.example.backend.model;


import jakarta.persistence.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@SuppressWarnings("unused")
@Entity
@Table(name="sdi_task")
public class Task implements HasId, Serializable {

    @Id
    @GeneratedValue
    @Column(name="t_id")
    private UUID id;

    @NotBlank(message = "Name must not be blank")
    @Column(name="t_name")
    private String name;

    @NotNull(message = "Description must not be null")
    @Column(name="t_description")
    private String description;

    @Max(value = 10, message = "Priority must be between 1 and 10")
    @Min(value = 1, message = "Priority must be between 1 and 10")
    @Column(name="t_priority")
    private byte priority;

    @NotNull(message = "Due date must not be null")
    @Column(name="t_due_date")
    private Instant dueDate;

    @NotNull(message = "User must not be null")
    @Column(name="t_user")
    private UUID user;

    public Task(String name, String description, byte priority, Instant dueDate, UUID user) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.user = user;
    }

    public Task() {

    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID uuid) {
        this.id = uuid;
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

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return """
                { "id":"%s", "name":"%s", "description":"%s", "priority":"%s", "dueDate":"%s, "user":"%s"}
                """.formatted(id, name, description, priority, dueDate, user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return getId() == task.getId();
    }

    public boolean validationFails() {
        return name == null
                || name.isEmpty()
                || description == null
                || dueDate == null
                || user == null;
    }
}
