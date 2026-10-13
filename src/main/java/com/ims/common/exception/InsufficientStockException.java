package com.ims.common.exception;

/**
 * Thrown by {@code StockService} when a requested OUT or TRANSFER quantity
 * would cause inventory to drop below zero.
 */
public class InsufficientStockException extends Exception {

    private final int available;
    private final int requested;

    public InsufficientStockException(String productName, int available, int requested) {
        super(String.format(
                "Insufficient stock for '%s': requested %d but only %d available.",
                productName, requested, available));
        this.available = available;
        this.requested = requested;
    }

    public int getAvailable() { return available; }
    public int getRequested() { return requested; }
}
