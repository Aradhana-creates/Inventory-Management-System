package com.pms.model;

import java.time.LocalDateTime;

/**
 * Model representing the 'warehouses' table.
 */
public class Warehouse {
    private int id;
    private String name;
    private String location;
    private int capacity;
    private boolean active;
    private LocalDateTime createdAt;

    public Warehouse() {}

    public Warehouse(int id, String name, String location, int capacity, boolean active) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.active = active;
    }

    public Warehouse(String name, String location, int capacity, boolean active) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Warehouse{id=" + id + ", name='" + name + "', location='" + location + "', capacity=" + capacity + ", active=" + active + "}";
    }
}
