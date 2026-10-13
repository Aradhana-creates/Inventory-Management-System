package com.ims.user.model;

import com.ims.common.model.Role;
import java.time.LocalDateTime;

/**
 * Represents a system user.  The {@code passwordHash} field is BCrypt-hashed
 * and must never appear in logs or be sent over the wire in plain text.
 */
public class User {

    private int id;
    private String username;
    private String passwordHash;   // BCrypt — do NOT log
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Default constructor (used by DAO). */
    public User() {}

    /** Convenience constructor for tests and seed operations. */
    public User(int id, String username, String passwordHash,
                String fullName, String email, Role role, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public int getId()                    { return id; }
    public String getUsername()           { return username; }
    public String getPasswordHash()       { return passwordHash; }
    public String getFullName()           { return fullName; }
    public String getEmail()              { return email; }
    public Role getRole()                 { return role; }
    public boolean isActive()             { return active; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }

    // ── Setters ────────────────────────────────────────────────────────────

    public void setId(int id)                           { this.id = id; }
    public void setUsername(String username)             { this.username = username; }
    public void setPasswordHash(String passwordHash)     { this.passwordHash = passwordHash; }
    public void setFullName(String fullName)             { this.fullName = fullName; }
    public void setEmail(String email)                   { this.email = email; }
    public void setRole(Role role)                       { this.role = role; }
    public void setActive(boolean active)                { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt)    { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)    { this.updatedAt = updatedAt; }

    /**
     * Returns a safe string for logging — never includes the password hash.
     */
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role
                + ", active=" + active + "}";
    }
}
