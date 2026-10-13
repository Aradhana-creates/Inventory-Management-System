package com.pms.model;

import java.time.LocalDateTime;

/**
 * Model representing the 'stock_movements' table.
 */
public class StockMovement {
    private int id;
    private int productId;
    private int warehouseId;
    private Integer toWarehouseId;
    private String movementType; // 'IN','OUT','TRANSFER','AUDIT'
    private int quantity;
    private String referenceNote;
    private int performedBy;
    private LocalDateTime movementDate;

    public StockMovement() {}

    public StockMovement(int id, int productId, int warehouseId, Integer toWarehouseId, String movementType, int quantity, String referenceNote, int performedBy, LocalDateTime movementDate) {
        this.id = id;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.toWarehouseId = toWarehouseId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceNote = referenceNote;
        this.performedBy = performedBy;
        this.movementDate = movementDate;
    }

    public StockMovement(int productId, int warehouseId, Integer toWarehouseId, String movementType, int quantity, String referenceNote, int performedBy) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.toWarehouseId = toWarehouseId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceNote = referenceNote;
        this.performedBy = performedBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    public Integer getToWarehouseId() { return toWarehouseId; }
    public void setToWarehouseId(Integer toWarehouseId) { this.toWarehouseId = toWarehouseId; }

    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReferenceNote() { return referenceNote; }
    public void setReferenceNote(String referenceNote) { this.referenceNote = referenceNote; }

    public int getPerformedBy() { return performedBy; }
    public void setPerformedBy(int performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }

    @Override
    public String toString() {
        return "StockMovement{id=" + id + ", productId=" + productId + ", warehouseId=" + warehouseId + ", type='" + movementType + "', quantity=" + quantity + "}";
    }
}
