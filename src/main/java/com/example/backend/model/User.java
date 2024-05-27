package com.example.backend.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@SuppressWarnings({"unused", "JpaDataSourceORMInspection"})
@Entity
@Table(name = "sdi_user")
public class User implements HasId {

    @Id
    @GeneratedValue
    @Column(name = "u_id")
    private UUID id;

    @NotBlank(message = "Username must not be blank")
    @NotNull(message = "Username must be present for any user")
    @Column(name = "u_username")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @NotNull(message = "Password must be present for any user")
    @Column(name = "u_password")
    private String password;

    @Column(name = "u_email")
    private String email;

    @Column(name = "u_role")
    private UUID role;


    public User() {

    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID newId) {
        id = newId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getRole() {
        return role;
    }

    public void setRole(UUID role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getId() == user.getId();
    }

    @Override
    public String toString() {
        return """
                {"id":"%s","name":"%s","password":"%s","email":"%s", "role":"%s"}
                """.formatted(id, username, password, email, role);
    }
}