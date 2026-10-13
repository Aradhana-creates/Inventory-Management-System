package com.pms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model representing the 'products' table.
 */
public class Product {
    private int id;
    private String sku;
    private String name;
    private String description;
    private Integer categoryId;
    private BigDecimal unitPrice;
    private int reorderLevel;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {}

    public Product(int id, String sku, String name, String description, Integer categoryId, BigDecimal unitPrice, int reorderLevel, boolean active) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
        this.active = active;
    }

    public Product(String sku, String name, String description, Integer categoryId, BigDecimal unitPrice, int reorderLevel, boolean active) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", sku='" + sku + "', name='" + name + "', unitPrice=" + unitPrice + ", reorderLevel=" + reorderLevel + ", active=" + active + "}";
    }
}
