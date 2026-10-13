package com.pms.model;

import java.time.LocalDateTime;

/**
 * Model representing the 'suppliers' table.
 */
public class Supplier {
    private int id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Supplier() {}

    public Supplier(int id, String name, String contactPerson, String phone, String email, String address, boolean active) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.active = active;
    }

    public Supplier(String name, String contactPerson, String phone, String email, String address, boolean active) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Supplier{id=" + id + ", name='" + name + "', contactPerson='" + contactPerson + "', phone='" + phone + "', active=" + active + "}";
    }
}
