package com.example.backend.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "sdi_role")
public class Role implements HasId, Serializable {

    @Id
    @GeneratedValue
    @Column(name = "r_id")
    private UUID id;

    @Column(name = "r_name")
    private String name;

    @OneToMany(mappedBy = "role")
    private Set<RolePermission> rolePermissions;

    public Role(String name) {
        id = UUID.randomUUID();
        this.name = name;
    }

    public Role() {

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
    public void setName(String subject) {
        this.name = subject;
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