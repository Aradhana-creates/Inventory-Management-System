package com.pms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing the 'sales' table.
 */
public class Sale {
    private int id;
    private String invoiceNumber;
    private int warehouseId;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalAmount;
    private LocalDateTime saleDate;
    private int createdBy;

    private List<SaleItem> items = new ArrayList<>();

    public Sale() {}

    public Sale(int id, String invoiceNumber, int warehouseId, String customerName, String customerPhone, BigDecimal totalAmount, LocalDateTime saleDate, int createdBy) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.warehouseId = warehouseId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Sale{id=" + id + ", invoiceNumber='" + invoiceNumber + "', totalAmount=" + totalAmount + ", saleDate=" + saleDate + "}";
    }
}
