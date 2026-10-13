# Inventory Management System — Entity-Relationship Diagram

```mermaid
erDiagram
    users {
        INT id PK
        VARCHAR username UK
        VARCHAR password_hash
        VARCHAR full_name
        VARCHAR email
        ENUM role
        TINYINT active
        DATETIME created_at
        DATETIME updated_at
    }

    categories {
        INT id PK
        VARCHAR name UK
        TEXT description
        DATETIME created_at
    }

    suppliers {
        INT id PK
        VARCHAR name
        VARCHAR contact_person
        VARCHAR phone
        VARCHAR email
        TEXT address
        TINYINT active
        DATETIME created_at
        DATETIME updated_at
    }

    warehouses {
        INT id PK
        VARCHAR name UK
        VARCHAR location
        INT capacity
        TINYINT active
        DATETIME created_at
    }

    products {
        INT id PK
        VARCHAR sku UK
        VARCHAR name
        TEXT description
        INT category_id FK
        DECIMAL unit_price
        INT reorder_level
        TINYINT active
        DATETIME created_at
        DATETIME updated_at
    }

    inventory {
        INT id PK
        INT product_id FK
        INT warehouse_id FK
        INT quantity
        DATETIME updated_at
    }

    stock_movements {
        INT id PK
        INT product_id FK
        INT warehouse_id FK
        INT to_warehouse_id FK
        ENUM movement_type
        INT quantity
        VARCHAR reference_note
        INT performed_by FK
        DATETIME movement_date
    }

    purchase_orders {
        INT id PK
        INT supplier_id FK
        INT warehouse_id FK
        ENUM status
        DATE order_date
        DATE expected_date
        DATE received_date
        TEXT notes
        INT created_by FK
        INT approved_by FK
        DATETIME created_at
        DATETIME updated_at
    }

    purchase_order_items {
        INT id PK
        INT po_id FK
        INT product_id FK
        INT quantity
        DECIMAL unit_price
    }

    sales {
        INT id PK
        VARCHAR invoice_number UK
        INT warehouse_id FK
        VARCHAR customer_name
        VARCHAR customer_phone
        DECIMAL total_amount
        DATETIME sale_date
        INT created_by FK
    }

    sale_items {
        INT id PK
        INT sale_id FK
        INT product_id FK
        INT quantity
        DECIMAL unit_price
    }

    %% Relationships
    categories       ||--o{ products              : "categorises"
    products         ||--o{ inventory             : "stocked in"
    warehouses       ||--o{ inventory             : "holds"
    products         ||--o{ stock_movements       : "tracked by"
    warehouses       ||--o{ stock_movements       : "source of"
    warehouses       ||--o{ stock_movements       : "destination of"
    users            ||--o{ stock_movements       : "performed by"
    suppliers        ||--o{ purchase_orders       : "fulfils"
    warehouses       ||--o{ purchase_orders       : "receives at"
    users            ||--o{ purchase_orders       : "created by"
    users            ||--o{ purchase_orders       : "approved by"
    purchase_orders  ||--o{ purchase_order_items  : "contains"
    products         ||--o{ purchase_order_items  : "ordered in"
    warehouses       ||--o{ sales                 : "sold from"
    users            ||--o{ sales                 : "created by"
    sales            ||--o{ sale_items            : "line items"
    products         ||--o{ sale_items            : "sold as"
```

## Table Summary

| Table | Purpose | Key Constraints |
|---|---|---|
| `users` | System users with roles | `username` UNIQUE; BCrypt password |
| `categories` | Product category lookup | `name` UNIQUE |
| `suppliers` | Supplier master | Soft-delete via `active` flag |
| `warehouses` | Physical warehouse locations | `name` UNIQUE; `capacity >= 0` |
| `products` | Product catalogue | `sku` UNIQUE; `unit_price >= 0`; `reorder_level >= 0` |
| `inventory` | Current stock per product × warehouse | `(product_id, warehouse_id)` UNIQUE; `quantity >= 0` |
| `stock_movements` | Immutable audit log of all stock changes | `quantity > 0`; FK to `to_warehouse_id` for TRANSFER |
| `purchase_orders` | PO header (supplier → warehouse) | Status enum: PENDING→APPROVED→RECEIVED / REJECTED |
| `purchase_order_items` | Line items on a PO | `quantity > 0`; cascades on PO delete |
| `sales` | Sales transaction header | `invoice_number` UNIQUE |
| `sale_items` | Line items on a sale | `quantity > 0`; cascades on sale delete |

## Design Decisions

- **`inventory` table** is the single source of truth for current stock. `StockService` always updates this table within a transaction alongside inserting a `stock_movements` record.
- **`stock_movements` are immutable** — records are never deleted or updated. Reversals are done by recording an opposite-direction movement.
- **`to_warehouse_id`** on `stock_movements` is only populated for `TRANSFER` type; NULL for IN/OUT/AUDIT.
- **Soft deletes** on `users`, `suppliers`, and `warehouses` via the `active` flag to preserve referential integrity.
