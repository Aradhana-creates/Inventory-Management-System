# Inventory Management System — Project Plan

> **Source of truth:** `docs/SRS.md` (primary) + `docs/SPP.md` (additive).
> **Conflict rule:** SRS wins; SPP extras are layered on top.

---

## 1. Module List and Requirements Covered

| # | Module | SRS Requirements | SPP Scope Items |
|---|--------|-----------------|-----------------|
| M1 | **Auth & Session** | FR1 (login, role routing) | Admin Login, Staff Login |
| M2 | **User Management** | FR1 (user accounts implied), §2.3 user roles | Manage users/staff (Admin) |
| M3 | **Category Management** | FR2 (Category field on Product) | Category Management |
| M4 | **Product Management** | FR2 (Add/Edit/Delete/Search, SKU, Reorder Level) | Product Management, Search & Filter Products |
| M5 | **Supplier Management** | FR3 (Register/Edit/Delete suppliers, link to POs) | Supplier Management |
| M6 | **Warehouse Management** | FR6 (multiple warehouses, stock per warehouse, audit) | — |
| M7 | **Stock Movement** | FR5 (IN/OUT/TRANSFER, history), §8 constraints | Stock In Module, Stock Out Module, Inventory Tracking |
| M8 | **Purchase Order** | FR4 (Create PO, approve/reject, status, update stock on receipt) | Stock In / Purchase Management |
| M9 | **Sales & Invoice** | FR7 (Record sale, reduce stock, generate invoice) | Stock Out / Sales Management |
| M10 | **Alerts** | FR8 (Low-stock / Reorder alerts) | Low-Stock Alerts |
| M11 | **Reports** | FR8 (Stock levels, PO history, movement log, sales summary) | Inventory Reports |
| M12 | **Database Backup** | — (implied by reliability NFR) | Database Backup |

---

## 2. Traceability Table

| Requirement | Module | Key Classes | Screen |
|-------------|--------|-------------|--------|
| FR1 – User Login | M1 Auth | `User`, `UserDao`, `AuthService`, `LoginController` | `login.fxml` |
| FR1 – Role routing | M1 Auth | `SessionManager`, `MainController` | `main.fxml` (sidebar) |
| SPP – User Management | M2 User Mgmt | `User`, `UserDao`, `UserService`, `UserController` | `user_management.fxml` |
| SPP – Category Mgmt | M3 Category | `Category`, `CategoryDao`, `CategoryService`, `CategoryController` | `category.fxml` |
| FR2 – Product CRUD | M4 Product | `Product`, `ProductDao`, `ProductService`, `ProductController` | `product.fxml` |
| FR2 – SKU / Reorder | M4 Product | `Product` (fields), `ProductService` (validation) | `product.fxml` |
| FR2 – Search/Filter | M4 Product | `ProductDao.search()`, `ProductController` | `product.fxml` (search bar) |
| FR3 – Supplier CRUD | M5 Supplier | `Supplier`, `SupplierDao`, `SupplierService`, `SupplierController` | `supplier.fxml` |
| FR4 – Create PO | M8 Purchase Order | `PurchaseOrder`, `PurchaseOrderDao`, `PurchaseOrderService`, `PurchaseOrderController` | `purchase_order.fxml` |
| FR4 – Approve/Reject PO | M8 Purchase Order | `PurchaseOrderService.approve()` / `.reject()` | `purchase_order.fxml` |
| FR4 – Status tracking | M8 Purchase Order | `PurchaseOrderStatus` (enum: PENDING/APPROVED/RECEIVED/REJECTED) | `purchase_order.fxml` |
| FR4 – Stock on receipt | M8 + M7 | `PurchaseOrderService` → `StockService.stockIn()` | `purchase_order.fxml` |
| FR5 – Stock IN | M7 Stock Movement | `StockMovement`, `StockMovementDao`, `StockService` | `stock_movement.fxml` |
| FR5 – Stock OUT | M7 Stock Movement | `StockService.stockOut()` | `stock_movement.fxml` |
| FR5 – Transfer | M7 Stock Movement | `StockService.transfer()` | `stock_movement.fxml` |
| FR5 – Movement history | M7 Stock Movement | `StockMovementDao.findAll()`, `StockMovementController` | `stock_movement.fxml` |
| FR6 – Warehouse CRUD | M6 Warehouse | `Warehouse`, `WarehouseDao`, `WarehouseService`, `WarehouseController` | `warehouse.fxml` |
| FR6 – Stock per warehouse | M6 + M7 | `InventoryDao` (product×warehouse stock table) | `warehouse.fxml` |
| FR6 – Stock audit | M6 + M7 | `StockService.audit()`, `MovementType.AUDIT` | `stock_movement.fxml` |
| FR7 – Record sale | M9 Sales | `Sale`, `SaleDao`, `SaleService`, `SalesController` | `sales.fxml` |
| FR7 – Reduce stock on sale | M9 + M7 | `SaleService` → `StockService.stockOut()` | `sales.fxml` |
| FR7 – Invoice | M9 Sales | `InvoiceService`, `InvoiceController` | `invoice.fxml` |
| FR8 – Low-stock alerts | M10 Alerts | `AlertService`, `AlertController` (dashboard badge) | `dashboard.fxml` |
| FR8 – Reports | M11 Reports | `ReportService`, `ReportController` | `reports.fxml` |
| SPP – Database Backup | M12 Backup | `BackupService`, `BackupController` | `backup.fxml` |

---

## 3. Maven Project — Package and Folder Structure

```
inventory-management-system/
├── pom.xml
├── docs/
│   ├── SRS.md
│   ├── SPP.md
│   ├── PLAN.md
│   └── PROGRESS.md
└── src/
    ├── main/
    │   ├── java/com/ims/
    │   │   ├── App.java                        ← JavaFX entry point
    │   │   ├── common/
    │   │   │   ├── db/
    │   │   │   │   ├── DatabaseConfig.java      ← HikariCP pool config
    │   │   │   │   └── DatabaseInitializer.java ← schema + seed on first run
    │   │   │   ├── model/
    │   │   │   │   └── Role.java               ← enum ADMIN/MANAGER/WAREHOUSE_STAFF/SALES_STAFF
    │   │   │   ├── exception/
    │   │   │   │   ├── AuthException.java
    │   │   │   │   ├── InsufficientStockException.java
    │   │   │   │   └── ValidationException.java
    │   │   │   └── util/
    │   │   │       ├── AlertUtil.java           ← friendly error/info dialogs
    │   │   │       ├── ValidationUtil.java
    │   │   │       └── FxmlLoader.java
    │   │   ├── auth/
    │   │   │   ├── model/SessionManager.java    ← singleton logged-in user
    │   │   │   ├── service/AuthService.java
    │   │   │   └── controller/LoginController.java
    │   │   ├── user/
    │   │   │   ├── model/User.java
    │   │   │   ├── dao/UserDao.java
    │   │   │   ├── service/UserService.java
    │   │   │   └── controller/UserController.java
    │   │   ├── category/
    │   │   │   ├── model/Category.java
    │   │   │   ├── dao/CategoryDao.java
    │   │   │   ├── service/CategoryService.java
    │   │   │   └── controller/CategoryController.java
    │   │   ├── product/
    │   │   │   ├── model/Product.java
    │   │   │   ├── dao/ProductDao.java
    │   │   │   ├── service/ProductService.java
    │   │   │   └── controller/ProductController.java
    │   │   ├── supplier/
    │   │   │   ├── model/Supplier.java
    │   │   │   ├── dao/SupplierDao.java
    │   │   │   ├── service/SupplierService.java
    │   │   │   └── controller/SupplierController.java
    │   │   ├── warehouse/
    │   │   │   ├── model/Warehouse.java
    │   │   │   ├── dao/WarehouseDao.java
    │   │   │   ├── service/WarehouseService.java
    │   │   │   └── controller/WarehouseController.java
    │   │   ├── stock/
    │   │   │   ├── model/
    │   │   │   │   ├── StockMovement.java
    │   │   │   │   ├── MovementType.java        ← enum IN/OUT/TRANSFER/AUDIT
    │   │   │   │   └── Inventory.java           ← product×warehouse current qty
    │   │   │   ├── dao/
    │   │   │   │   ├── StockMovementDao.java
    │   │   │   │   └── InventoryDao.java
    │   │   │   ├── service/StockService.java    ← SINGLE entry point for all stock changes
    │   │   │   └── controller/StockMovementController.java
    │   │   ├── purchaseorder/
    │   │   │   ├── model/
    │   │   │   │   ├── PurchaseOrder.java
    │   │   │   │   ├── PurchaseOrderItem.java
    │   │   │   │   └── PurchaseOrderStatus.java ← enum PENDING/APPROVED/RECEIVED/REJECTED
    │   │   │   ├── dao/PurchaseOrderDao.java
    │   │   │   ├── service/PurchaseOrderService.java
    │   │   │   └── controller/PurchaseOrderController.java
    │   │   ├── sales/
    │   │   │   ├── model/
    │   │   │   │   ├── Sale.java
    │   │   │   │   └── SaleItem.java
    │   │   │   ├── dao/SaleDao.java
    │   │   │   ├── service/SaleService.java
    │   │   │   └── controller/
    │   │   │       ├── SalesController.java
    │   │   │       └── InvoiceController.java
    │   │   ├── alert/
    │   │   │   ├── service/AlertService.java
    │   │   │   └── controller/AlertController.java
    │   │   ├── report/
    │   │   │   ├── service/ReportService.java
    │   │   │   └── controller/ReportController.java
    │   │   ├── backup/
    │   │   │   ├── service/BackupService.java
    │   │   │   └── controller/BackupController.java
    │   │   └── main/
    │   │       └── controller/MainController.java ← sidebar + scene switcher
    │   └── resources/
    │       ├── com/ims/fxml/
    │       │   ├── login.fxml
    │       │   ├── main.fxml                   ← shell with sidebar
    │       │   ├── dashboard.fxml
    │       │   ├── user_management.fxml
    │       │   ├── category.fxml
    │       │   ├── product.fxml
    │       │   ├── supplier.fxml
    │       │   ├── warehouse.fxml
    │       │   ├── stock_movement.fxml
    │       │   ├── purchase_order.fxml
    │       │   ├── sales.fxml
    │       │   ├── invoice.fxml
    │       │   ├── reports.fxml
    │       │   └── backup.fxml
    │       ├── com/ims/css/
    │       │   └── style.css
    │       ├── com/ims/images/
    │       │   └── logo.png
    │       └── db/
    │           ├── schema.sql
    │           └── seed.sql                    ← default admin user + demo data
    └── test/
        └── java/com/ims/
            ├── auth/AuthServiceTest.java
            ├── product/ProductServiceTest.java
            ├── stock/StockServiceTest.java
            ├── purchaseorder/PurchaseOrderServiceTest.java
            └── sales/SaleServiceTest.java
```

---

## 4. Role-Permission Matrix

> ✅ = full access | 👁 = view only | ➕ = create only | ❌ = no access

| Screen / Action | ADMIN | MANAGER | WAREHOUSE_STAFF | SALES_STAFF |
|---|:---:|:---:|:---:|:---:|
| **Login** | ✅ | ✅ | ✅ | ✅ |
| **Dashboard** (summary widgets + alerts) | ✅ | ✅ | ✅ | ✅ |
| **User Management** — view | ✅ | ❌ | ❌ | ❌ |
| **User Management** — create/edit/deactivate | ✅ | ❌ | ❌ | ❌ |
| **Category** — view | ✅ | ✅ | 👁 | 👁 |
| **Category** — create/edit/delete | ✅ | ✅ | ❌ | ❌ |
| **Product** — view / search | ✅ | ✅ | ✅ | ✅ |
| **Product** — create/edit/delete | ✅ | ✅ | ❌ | ❌ |
| **Supplier** — view | ✅ | ✅ | ✅ | ❌ |
| **Supplier** — create/edit/delete | ✅ | ✅ | ❌ | ❌ |
| **Warehouse** — view | ✅ | ✅ | ✅ | ❌ |
| **Warehouse** — create/edit/delete | ✅ | ✅ | ❌ | ❌ |
| **Stock Movement** — view history | ✅ | ✅ | ✅ | ❌ |
| **Stock Movement** — record IN/OUT/TRANSFER | ✅ | ✅ | ✅ | ❌ |
| **Stock Movement** — audit entry | ✅ | ✅ | ✅ | ❌ |
| **Purchase Order** — view | ✅ | ✅ | ✅ | ❌ |
| **Purchase Order** — create | ✅ | ✅ | ❌ | ❌ |
| **Purchase Order** — approve/reject | ✅ | ✅ | ❌ | ❌ |
| **Purchase Order** — mark received (stock-in) | ✅ | ✅ | ✅ | ❌ |
| **Sales** — view | ✅ | ✅ | ❌ | ✅ |
| **Sales** — record sale | ✅ | ✅ | ❌ | ✅ |
| **Sales** — generate invoice | ✅ | ✅ | ❌ | ✅ |
| **Alerts** — view low-stock | ✅ | ✅ | ✅ | ❌ |
| **Reports** — all reports | ✅ | ✅ | ❌ | ❌ |
| **Reports** — sales summary only | ✅ | ✅ | ❌ | 👁 |
| **Database Backup** | ✅ | ❌ | ❌ | ❌ |
| **Change Password** (own account) | ✅ | ✅ | ✅ | ✅ |

---

## 5. Gaps, Contradictions, and Assumptions

| # | Source | Issue | Assumption / Resolution |
|---|--------|-------|------------------------|
| G1 | SRS §2.4 | Mentions Apache Tomcat (web app), but fixed decision is JavaFX desktop. | Build JavaFX desktop app only. Tomcat reference is ignored. |
| G2 | SRS FR2 | `Category` is listed as a Product *field* (varchar), not a managed entity. SPP adds explicit Category Management module. | Implement `Category` as a first-class table with FK from `Product`. SRS field becomes a FK relationship. |
| G3 | SRS §6 | `Product` table has no `warehouse_id` — stock is not per-warehouse in the schema. FR6 requires per-warehouse stock. | Add a separate `inventory` table (`product_id`, `warehouse_id`, `quantity`) as the source of truth for current stock. `Product` holds metadata only. |
| G4 | SRS FR4 | PO table has only one `Product_ID` (single line item). Real POs have multiple items. | Implement `purchase_order_items` child table; the parent `purchase_orders` holds header data. |
| G5 | SRS FR7 | `Sales` table has no `Customer` or `invoice_number` columns, yet FR7 requires invoice generation and §5 lists "Customer Details" as input. | Add `customer_name`, `customer_phone`, and `invoice_number` (auto-generated) to the `sales` table; add `sale_items` child table. |
| G6 | SRS §8 | "Purchase orders below the reorder threshold require manager approval." This is ambiguous — all POs start PENDING and require approval. | All POs start as PENDING. Only ADMIN and MANAGER can approve/reject. No special logic tied to reorder threshold for approval gating. |
| G7 | SPP §5 | SPP gives MANAGER only view access (no create/edit). SRS §2.3 gives MANAGER the ability to "approve POs" and "manage warehouse operations". | MANAGER gets create + approve + management rights in line with SRS §2.3. SPP view-only is too restrictive. |
| G8 | SRS §8 | "Stock movement records cannot be deleted once confirmed, only reversed via a new entry." No reversal mechanism is defined. | Implement an `AUDIT` movement type that adjusts stock to a counted value. Manual reversal of IN/OUT is done by recording an opposite movement with a reference note. |
| G9 | SRS / SPP | Neither document specifies password reset flow (admin resets other users' passwords). | ADMIN can set a new (BCrypt-hashed) password for any user. Self-service "change own password" is available to all roles. |
| G10 | SPP | "Database Backup" is listed but no format or mechanism defined. | `BackupService` will invoke `mysqldump` as a system process and save a `.sql` file to a user-chosen directory. ADMIN only. |
| G11 | SRS FR8 | Alerts are listed in the same FR as Reports but are a distinct runtime concern. | `AlertService` runs on dashboard load, queries products where `current_stock ≤ reorder_level`, and surfaces results as a live badge/list. Separate from static report generation. |

---

## 6. Build Order and Checkpoints

Each step ends with: **`mvn package` → run tests → launch app → honest status report**.

| Step | What Gets Built | Runnable Checkpoint |
|------|----------------|---------------------|
| **Step 1** — Foundation | `pom.xml`, `schema.sql`, `seed.sql`, `DatabaseConfig` (HikariCP), `DatabaseInitializer`, `Role` enum, `App.java`, blank `main.fxml` + sidebar shell, `style.css` skeleton | App launches, shows an empty sidebar window; DB connection confirmed in logs. |
| **Step 2** — Auth | `User` model, `UserDao`, `AuthService` (BCrypt), `SessionManager`, `LoginController`, `login.fxml` | Login screen appears; valid credentials route to dashboard; invalid credentials show friendly error dialog. |
| **Step 3** — User Management | `UserService`, `UserController`, `user_management.fxml` | ADMIN can create/edit/deactivate users; other roles see the screen hidden in sidebar. |
| **Step 4** — Category | `Category` model/DAO/service/controller, `category.fxml` | ADMIN/MANAGER can add/edit/delete categories; table displays correctly. |
| **Step 5** — Product | `Product` model/DAO/service/controller, `product.fxml` with search/filter bar | Full CRUD + search; SKU uniqueness enforced; category FK dropdown populated. |
| **Step 6** — Supplier & Warehouse | Both modules (model/DAO/service/controller/fxml) | Supplier and Warehouse screens fully functional; role restrictions enforced. |
| **Step 7** — Stock & Inventory | `Inventory` table + DAO, `StockMovement` + DAO, `StockService` (IN/OUT/TRANSFER/AUDIT, transactions, no-negative guard), `stock_movement.fxml` | Stock changes recorded; stock cannot go below 0; history table visible. |
| **Step 8** — Purchase Orders | `PurchaseOrder` + `PurchaseOrderItem`, DAO, `PurchaseOrderService` (approve → `StockService.stockIn()`), `purchase_order.fxml` | Full PO lifecycle: create → approve → receive (stock auto-increases). |
| **Step 9** — Sales & Invoice | `Sale` + `SaleItem`, `SaleService` (→ `StockService.stockOut()`), `InvoiceService`, `sales.fxml`, `invoice.fxml` | Sale recorded, stock decreases, invoice preview shown; insufficient stock rejected. |
| **Step 10** — Alerts & Dashboard | `AlertService`, dashboard widgets (low-stock badge, summary counts), `dashboard.fxml` | Dashboard shows live counts; low-stock products listed with reorder level. |
| **Step 11** — Reports | `ReportService` (stock levels, PO history, movement log, sales summary), `reports.fxml` with table + export-to-CSV | All four report types generate correctly; CSV export works. |
| **Step 12** — Database Backup | `BackupService` (mysqldump subprocess), `backup.fxml` | ADMIN triggers backup; `.sql` file saved to chosen path; success/failure dialog shown. |
| **Step 13** — Polish & Tests | `style.css` finalised, all JUnit 5 tests passing, window sizes consistent, no overlapping controls | `mvn test` green; full end-to-end demo walkthrough passes. |
