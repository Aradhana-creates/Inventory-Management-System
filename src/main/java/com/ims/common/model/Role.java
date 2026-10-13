package com.ims.common.model;

/**
 * Application roles that map directly to the {@code role} ENUM column in the
 * {@code users} table.  The names are stored in MySQL verbatim.
 */
public enum Role {

    /** Full system access including user management and database backup. */
    ADMIN,

    /** Can approve purchase orders, view all reports, and manage inventory. */
    MANAGER,

    /** Records stock movements, processes purchase order receipts. */
    WAREHOUSE_STAFF,

    /** Records product sales and views product availability. */
    SALES_STAFF;

    /** Returns a human-readable display label for use in the UI. */
    public String getDisplayName() {
        return switch (this) {
            case ADMIN          -> "Administrator";
            case MANAGER        -> "Manager";
            case WAREHOUSE_STAFF -> "Warehouse Staff";
            case SALES_STAFF    -> "Sales Staff";
        };
    }
}
