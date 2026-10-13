-- =============================================================================
-- Inventory Management System — Database Schema
-- Target: MySQL 8.0+
-- Run:    mysql -u root -p ims_db < schema.sql
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------------------------
-- Table: roles (lookup)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id          TINYINT      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: users
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id              INT          NOT NULL AUTO_INCREMENT,
    username        VARCHAR(50)  NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,              -- BCrypt, never plain
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100),
    role            ENUM('ADMIN','MANAGER','WAREHOUSE_STAFF','SALES_STAFF') NOT NULL,
    active          TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: categories
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id          INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: suppliers
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS suppliers (
    id              INT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(150) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT,
    active          TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: warehouses
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS warehouses (
    id          INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(150) NOT NULL,
    location    VARCHAR(255),
    capacity    INT          NOT NULL DEFAULT 0 CHECK (capacity >= 0),
    active      TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_warehouses_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: products
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id              INT             NOT NULL AUTO_INCREMENT,
    sku             VARCHAR(50)     NOT NULL,
    name            VARCHAR(150)    NOT NULL,
    description     TEXT,
    category_id     INT,
    unit_price      DECIMAL(12,2)   NOT NULL DEFAULT 0.00 CHECK (unit_price >= 0),
    reorder_level   INT             NOT NULL DEFAULT 0  CHECK (reorder_level >= 0),
    active          TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_products_sku (sku),
    KEY idx_products_category (category_id),
    KEY idx_products_name (name),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: inventory  (current stock per product × warehouse)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS inventory (
    id              INT  NOT NULL AUTO_INCREMENT,
    product_id      INT  NOT NULL,
    warehouse_id    INT  NOT NULL,
    quantity        INT  NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_inventory_product_warehouse (product_id, warehouse_id),
    KEY idx_inventory_warehouse (warehouse_id),
    CONSTRAINT fk_inventory_product   FOREIGN KEY (product_id)   REFERENCES products   (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_inventory_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: stock_movements
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS stock_movements (
    id                  INT         NOT NULL AUTO_INCREMENT,
    product_id          INT         NOT NULL,
    warehouse_id        INT         NOT NULL,
    to_warehouse_id     INT,                               -- populated on TRANSFER
    movement_type       ENUM('IN','OUT','TRANSFER','AUDIT') NOT NULL,
    quantity            INT         NOT NULL CHECK (quantity > 0),
    reference_note      VARCHAR(255),                      -- PO#, Sale#, manual note
    performed_by        INT         NOT NULL,              -- FK → users.id
    movement_date       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_sm_product  (product_id),
    KEY idx_sm_warehouse (warehouse_id),
    KEY idx_sm_date     (movement_date),
    CONSTRAINT fk_sm_product     FOREIGN KEY (product_id)      REFERENCES products   (id) ON UPDATE CASCADE,
    CONSTRAINT fk_sm_warehouse   FOREIGN KEY (warehouse_id)    REFERENCES warehouses (id) ON UPDATE CASCADE,
    CONSTRAINT fk_sm_to_wh       FOREIGN KEY (to_warehouse_id) REFERENCES warehouses (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_sm_user        FOREIGN KEY (performed_by)    REFERENCES users      (id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: purchase_orders
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchase_orders (
    id              INT         NOT NULL AUTO_INCREMENT,
    supplier_id     INT         NOT NULL,
    warehouse_id    INT         NOT NULL,              -- receiving warehouse
    status          ENUM('PENDING','APPROVED','RECEIVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    order_date      DATE        NOT NULL,
    expected_date   DATE,
    received_date   DATE,
    notes           TEXT,
    created_by      INT         NOT NULL,
    approved_by     INT,
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_po_supplier (supplier_id),
    KEY idx_po_status   (status),
    CONSTRAINT fk_po_supplier    FOREIGN KEY (supplier_id)  REFERENCES suppliers  (id) ON UPDATE CASCADE,
    CONSTRAINT fk_po_warehouse   FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON UPDATE CASCADE,
    CONSTRAINT fk_po_created_by  FOREIGN KEY (created_by)   REFERENCES users      (id) ON UPDATE CASCADE,
    CONSTRAINT fk_po_approved_by FOREIGN KEY (approved_by)  REFERENCES users      (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: purchase_order_items
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchase_order_items (
    id              INT             NOT NULL AUTO_INCREMENT,
    po_id           INT             NOT NULL,
    product_id      INT             NOT NULL,
    quantity        INT             NOT NULL CHECK (quantity > 0),
    unit_price      DECIMAL(12,2)   NOT NULL DEFAULT 0.00 CHECK (unit_price >= 0),
    PRIMARY KEY (id),
    KEY idx_poi_po      (po_id),
    KEY idx_poi_product (product_id),
    CONSTRAINT fk_poi_po      FOREIGN KEY (po_id)       REFERENCES purchase_orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_poi_product FOREIGN KEY (product_id)  REFERENCES products        (id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: sales
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sales (
    id              INT             NOT NULL AUTO_INCREMENT,
    invoice_number  VARCHAR(30)     NOT NULL,
    warehouse_id    INT             NOT NULL,
    customer_name   VARCHAR(150),
    customer_phone  VARCHAR(20),
    total_amount    DECIMAL(12,2)   NOT NULL DEFAULT 0.00 CHECK (total_amount >= 0),
    sale_date       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      INT             NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_sales_invoice (invoice_number),
    KEY idx_sales_date      (sale_date),
    KEY idx_sales_warehouse (warehouse_id),
    CONSTRAINT fk_sales_warehouse  FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON UPDATE CASCADE,
    CONSTRAINT fk_sales_created_by FOREIGN KEY (created_by)   REFERENCES users      (id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: sale_items
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sale_items (
    id          INT             NOT NULL AUTO_INCREMENT,
    sale_id     INT             NOT NULL,
    product_id  INT             NOT NULL,
    quantity    INT             NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(12,2)   NOT NULL DEFAULT 0.00 CHECK (unit_price >= 0),
    PRIMARY KEY (id),
    KEY idx_si_sale    (sale_id),
    KEY idx_si_product (product_id),
    CONSTRAINT fk_si_sale    FOREIGN KEY (sale_id)    REFERENCES sales    (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_si_product FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
