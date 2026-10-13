package com.pms.model;

import java.math.BigDecimal;

/**
 * Model representing the 'sale_items' table.
 */
public class SaleItem {
    private int id;
    private int saleId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;

    public SaleItem() {}

    public SaleItem(int id, int saleId, int productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public SaleItem(int saleId, int productId, int quantity, BigDecimal unitPrice) {
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public SaleItem(int productId, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return "SaleItem{id=" + id + ", saleId=" + saleId + ", productId=" + productId + ", quantity=" + quantity + ", unitPrice=" + unitPrice + "}";
    }
}
