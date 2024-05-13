package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@SuppressWarnings("unused")
@Entity
public class User implements HasId {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Username must not be blank")
    @NotNull(message = "Username must be present for any user")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @NotNull(message = "Password must be present for any user")
    private String password;

    private String email;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getId() == user.getId();
    }

    @Override
    public String toString() {
        return """
                {
                    "id":"%s",
                    "name":"%s",
                    "password":"%s",
                    "email":"%s"
                }""".formatted(id, username, password, email);
    }
}
