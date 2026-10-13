package com.pms.model;

import java.time.LocalDateTime;

/**
 * Model representing the 'inventory' table.
 */
public class Inventory {
    private int id;
    private int productId;
    private int warehouseId;
    private int quantity;
    private LocalDateTime updatedAt;

    public Inventory() {}

    public Inventory(int id, int productId, int warehouseId, int quantity) {
        this.id = id;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
    }

    public Inventory(int productId, int warehouseId, int quantity) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Inventory{id=" + id + ", productId=" + productId + ", warehouseId=" + warehouseId + ", quantity=" + quantity + "}";
    }
}
