package com.pms.model;

import java.math.BigDecimal;

/**
 * Model representing the 'purchase_order_items' table.
 */
public class PurchaseOrderItem {
    private int id;
    private int poId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;

    public PurchaseOrderItem() {}

    public PurchaseOrderItem(int id, int poId, int productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.poId = poId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public PurchaseOrderItem(int poId, int productId, int quantity, BigDecimal unitPrice) {
        this.poId = poId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public PurchaseOrderItem(int productId, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPoId() { return poId; }
    public void setPoId(int poId) { this.poId = poId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return "PurchaseOrderItem{id=" + id + ", poId=" + poId + ", productId=" + productId + ", quantity=" + quantity + ", unitPrice=" + unitPrice + "}";
    }
}
