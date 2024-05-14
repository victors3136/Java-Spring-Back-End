package com.example.backend.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name="sdi_subtask")
public class Subtask implements HasId, Serializable {

    @Id
    @GeneratedValue
    @Column(name = "s_id")
    private UUID id;

    @NotBlank(message = "Subject must not be blank")
    @Column(name = "s_subject")
    private String subject;

    @Column(name = "s_task")
    private UUID task;

    public Subtask(String subject, UUID parentTask) {
        id = UUID.randomUUID();
        this.subject = subject;
        task = parentTask;
    }

    public Subtask() {

    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID newId) {
        id = newId;
    }

    public String getSubject() {
        return subject;
    }

    @SuppressWarnings("unused")
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public UUID getTask() {
        return task;
    }

    @SuppressWarnings("unused")
    public void setTask(UUID task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return ("""
                {"id"="%s","subject"="%s","task"="%s"}
                """).formatted(id, subject, task);
    }

    public boolean validationFails() {
        return subject == null
                || subject.isEmpty()
                || id == null
                || task == null;
    }
}
