# Inventory Management System — Progress Tracker

> Last updated: 2026-10-08
> Build step references match `docs/PLAN.md §6`.

---

## Legend
| Symbol | Meaning |
|--------|---------|
| ✅ | Complete and verified |
| 🔄 | In progress |
| ⬜ | Not started |
| ❌ | Blocked / failed |

---

## Build Steps

| Step | Description | Status | Notes |
|------|-------------|--------|-------|
| **Step 1** | Foundation (pom.xml, DB config, App.java, shell UI) | 🔄 | pom.xml, HikariCP, shared models and DAO layer built |
| **DAO Layer** | All DAOs in com.pms.dao + com.ims | ✅ | UserDAO, CategoryDAO, ProductDAO, SupplierDAO, WarehouseDAO, InventoryDAO, PurchaseOrderDAO, SaleDAO, StockMovementDAO (9/9 JUnit tests passing) |
| **UI Controllers** | Controllers in com.pms.ui | ✅ | LoginController, MainDashboardController, ProductController, InventoryController, SalesController |
| **JavaFX Application** | ProductManagementApp & FXML Views | ✅ | ProductManagementApp entry point configured with scene switching; all 5 FXML files in src/main/resources/com/pms/ui verified (15/15 JUnit tests passing) |
| **Demo Seeder** | SQL script & DatabaseSeeder utility | ✅ | demo_seed.sql loaded into ims_db; DatabaseSeeder utility created; 4 BCrypt users, 5 categories, 2 warehouses, 10 products, 14 inventory counts verified |
| **Step 2** | Auth module (Login screen, BCrypt, SessionManager) | ⬜ | — |
| **Step 3** | User Management (ADMIN CRUD) | ⬜ | — |
| **Step 4** | Category module | ⬜ | — |
| **Step 5** | Product module (CRUD + search/filter) | ⬜ | — |
| **Step 6** | Supplier + Warehouse modules | ⬜ | — |
| **Step 7** | Stock Movement + StockService (central transaction) | ⬜ | — |
| **Step 8** | Purchase Orders (full lifecycle) | ⬜ | — |
| **Step 9** | Sales + Invoice | ⬜ | — |
| **Step 10** | Alerts + Dashboard widgets | ⬜ | — |
| **Step 11** | Reports + CSV export | ⬜ | — |
| **Step 12** | Database Backup (mysqldump) | ⬜ | — |
| **Step 13** | Polish, tests green, end-to-end demo | ⬜ | — |

---

## Pre-Step: Database Setup ✅

| Task | Status | Detail |
|------|--------|--------|
| `docs/PLAN.md` created | ✅ | Module list, traceability, package tree, role matrix, gaps, build order |
| `docs/ER.md` created | ✅ | Mermaid ER diagram, 11 tables, design decisions |
| `src/main/resources/db/schema.sql` | ✅ | 11 tables, PKs, FKs, UNIQUE constraints, CHECK constraints, indexes |
| `src/main/resources/db/seed.sql` | ✅ | 4 users (real BCrypt `$2a$10$`), 5 categories, 6 suppliers, 3 warehouses, 25 products, 27 inventory rows |
| `ims_db` database created | ✅ | `mysql -u root -pIms12345` — exit 0 |
| `schema.sql` loaded | ✅ | Exit 0, no errors |
| `seed.sql` loaded | ✅ | Exit 0, no errors |

### Verified Table Counts

| Table | Expected | Actual | Status |
|-------|----------|--------|--------|
| `users` | 4 | 4 | ✅ |
| `categories` | 5 | 5 | ✅ |
| `suppliers` | 6 | 6 | ✅ |
| `warehouses` | 3 | 3 | ✅ |
| `products` | 25 | 25 | ✅ |
| `inventory` | 27 | 27 | ✅ |
| `stock_movements` | 0 | 0 | ✅ (empty — correct) |
| `purchase_orders` | 0 | 0 | ✅ (empty — correct) |
| `sale_items` | 0 | 0 | ✅ (empty — correct) |

### Verified: 3 Products Below Reorder Level

| SKU | Product | Reorder Level | Stock | Result |
|-----|---------|--------------|-------|--------|
| EL-001 | Laptop 15" | 5 | 3 | ⚠ BELOW |
| FU-002 | Executive Desk | 3 | 1 | ⚠ BELOW |
| TL-001 | Cordless Drill | 5 | 2 | ⚠ BELOW |

### Verified: User Roles and BCrypt Hashes

| Username | Role | Hash Prefix | Active |
|----------|------|-------------|--------|
| admin | ADMIN | `$2a$10$gsFUh...` | ✅ |
| manager | MANAGER | `$2a$10$LM2i0...` | ✅ |
| warehouse | WAREHOUSE_STAFF | `$2a$10$JeMOG...` | ✅ |
| sales | SALES_STAFF | `$2a$10$fRmd5...` | ✅ |

---

## Requirements Traceability

| Requirement | Module | Status |
|-------------|--------|--------|
| FR1 — User Login | M1 Auth | ⬜ |
| FR2 — Product Management | M4 Product | ⬜ |
| FR3 — Supplier Management | M5 Supplier | ⬜ |
| FR4 — Purchase Order Management | M8 Purchase Order | ⬜ |
| FR5 — Stock Movement Tracking | M7 Stock | ⬜ |
| FR6 — Warehouse Operations | M6 Warehouse | ⬜ |
| FR7 — Sales Management | M9 Sales | ⬜ |
| FR8 — Alerts & Reports | M10 Alerts + M11 Reports | ⬜ |
| SPP — Category Management | M3 Category | ⬜ |
| SPP — User Management | M2 User | ⬜ |
| SPP — Database Backup | M12 Backup | ⬜ |

---

## Known Issues / Decisions

- BCrypt prefix: seed uses `$2a$10$` (jbcrypt-compatible). Python's bcrypt library generates `$2b$10$` — manually converted in seed.sql.
- `stock_movements.quantity` CHECK `> 0` (positive): reversals are separate opposite-direction records, not negative quantities.
- `inventory.quantity` CHECK `>= 0`: enforced at DB level AND in `StockService` application layer.
