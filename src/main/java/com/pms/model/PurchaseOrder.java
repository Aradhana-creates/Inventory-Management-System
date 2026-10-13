package com.pms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing the 'purchase_orders' table.
 */
public class PurchaseOrder {
    private int id;
    private int supplierId;
    private int warehouseId;
    private String status; // 'PENDING','APPROVED','RECEIVED','REJECTED'
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private LocalDate receivedDate;
    private String notes;
    private int createdBy;
    private Integer approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PurchaseOrderItem> items = new ArrayList<>();

    public PurchaseOrder() {}

    public PurchaseOrder(int id, int supplierId, int warehouseId, String status, LocalDate orderDate, LocalDate expectedDate, LocalDate receivedDate, String notes, int createdBy, Integer approvedBy) {
        this.id = id;
        this.supplierId = supplierId;
        this.warehouseId = warehouseId;
        this.status = status;
        this.orderDate = orderDate;
        this.expectedDate = expectedDate;
        this.receivedDate = receivedDate;
        this.notes = notes;
        this.createdBy = createdBy;
        this.approvedBy = approvedBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public LocalDate getExpectedDate() { return expectedDate; }
    public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public Integer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Integer approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<PurchaseOrderItem> getItems() { return items; }
    public void setItems(List<PurchaseOrderItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "PurchaseOrder{id=" + id + ", supplierId=" + supplierId + ", status='" + status + "', orderDate=" + orderDate + "}";
    }
}
