package com.pms.model;

import java.time.LocalDateTime;

/**
 * Model representing the 'users' table.
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public User(int id, String username, String passwordHash, String fullName, String email, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public User(String username, String passwordHash, String fullName, String email, String role, boolean active) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', fullName='" + fullName + "', role='" + role + "', active=" + active + "}";
    }
}
