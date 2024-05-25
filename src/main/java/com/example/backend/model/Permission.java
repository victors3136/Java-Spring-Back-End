package com.example.backend.model;

import jakarta.persistence.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "sdi_permission")
public class Permission implements HasId, Serializable {

    @Id
    @GeneratedValue
    @Column(name = "p_id")
    private UUID id;

    @Column(name = "p_name")
    private String name;


    public Permission(String name) {
        id = UUID.randomUUID();
        this.name = name;
    }

    public Permission() {

    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID newId) {
        id = newId;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ("""
                {"id"="%s","name"="%s"}
                """).formatted(id, name);
    }

    public boolean validationFails() {
        return name == null
                || name.isEmpty()
                || id == null;
    }
}