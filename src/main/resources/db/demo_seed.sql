-- =============================================================================
-- Inventory Management System — Demo Seeder Script
-- Database: ims_db
-- Populates:
--   - 4 Test Users (admin, manager, sales, warehouse) with real BCrypt passwords
--   - 5 Product Categories
--   - 2 Warehouses
--   - 4 Suppliers
--   - 10 Sample Products
--   - Initial Inventory stock counts across warehouses (with low stock samples)
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------------------------
-- Clean previous demonstration data
-- -----------------------------------------------------------------------------
TRUNCATE TABLE sale_items;
TRUNCATE TABLE sales;
TRUNCATE TABLE purchase_order_items;
TRUNCATE TABLE purchase_orders;
TRUNCATE TABLE stock_movements;
TRUNCATE TABLE inventory;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE warehouses;
TRUNCATE TABLE suppliers;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- 1. Test Users (BCrypt Hashes, Cost factor 10)
-- Credentials:
--   admin     / admin123     (Role: ADMIN)
--   manager   / manager123   (Role: MANAGER)
--   sales     / sales123     (Role: SALES_STAFF)
--   warehouse / staff123     (Role: WAREHOUSE_STAFF)
-- -----------------------------------------------------------------------------
INSERT INTO users (id, username, password_hash, full_name, email, role, active) VALUES
(1, 'admin',     '$2a$10$gsFUhTupQe05Els86ps24eujanVsC9D73UUFkTDOjBoAnD/6kKrsa', 'System Administrator', 'admin@ims.local',     'ADMIN',           1),
(2, 'manager',   '$2a$10$LM2i0Vvr5FbZXmvBOgnMqexpXj4Z5sgEXHH7xYEW6iM3KF7ACWQpe', 'Operations Manager',   'manager@ims.local',   'MANAGER',         1),
(3, 'sales',     '$2a$10$fRmd5a26WYxW9AQZyb6l3OroWrQT6/DwsdOyB.yeyi1BjfaSYCh66', 'Sales Representative', 'sales@ims.local',     'SALES_STAFF',     1),
(4, 'warehouse', '$2a$10$JeMOGQp1lPn5lMWbA89pw.Rq9h/J3S8cYMb/QfxJi/bcqfEc4n2eu', 'Inventory Specialist', 'warehouse@ims.local', 'WAREHOUSE_STAFF', 1);

-- -----------------------------------------------------------------------------
-- 2. 5 Product Categories
-- -----------------------------------------------------------------------------
INSERT INTO categories (id, name, description) VALUES
(1, 'Electronics',      'Computers, peripherals, and electronic equipment'),
(2, 'Office Supplies',  'Paper, writing instruments, and office stationery'),
(3, 'Furniture',        'Desks, ergonomic chairs, and filing units'),
(4, 'Tools & Hardware', 'Maintenance tools, fasteners, and accessories'),
(5, 'Cleaning Supplies','Sanitation items, detergents, and facility supplies');

-- -----------------------------------------------------------------------------
-- 3. 2 Warehouses
-- -----------------------------------------------------------------------------
INSERT INTO warehouses (id, name, location, capacity, active) VALUES
(1, 'Main Central Warehouse',    'Zone A, 45 Logistics Hub, Chennai', 15000, 1),
(2, 'North Distribution Center', 'Sector 18, Industrial Estate, Delhi',  8000, 1);

-- -----------------------------------------------------------------------------
-- 4. Sample Suppliers
-- -----------------------------------------------------------------------------
INSERT INTO suppliers (id, name, contact_person, phone, email, address, active) VALUES
(1, 'Global Tech Supplies', 'Ravi Kumar',   '9811223344', 'ravi@globaltech.in',   'Tech Park, Bangalore', 1),
(2, 'Apex Office Depot',    'Priya Sharma', '9822334455', 'priya@apexoffice.in',  'Park Street, Kolkata', 1),
(3, 'Ergo Comfort Co.',     'Anil Mehta',   '9833445566', 'anil@ergocomfort.in',  'MIDC Industrial, Pune', 1),
(4, 'Industrial Tools Inc', 'Manoj Tiwari', '9844556677', 'manoj@indtools.in',    'Karol Bagh, Delhi',    1);

-- -----------------------------------------------------------------------------
-- 5. 10 Sample Products
-- -----------------------------------------------------------------------------
INSERT INTO products (id, sku, name, description, category_id, unit_price, reorder_level, active) VALUES
(1,  'EL-001', 'UltraBook Pro 15"',        '15.6-inch Intel Core i7, 16GB RAM, 512GB SSD', 1, 62000.00,  5, 1),
(2,  'EL-002', 'Wireless Ergonomic Mouse', '2.4GHz USB wireless optical mouse',             1,   899.00, 20, 1),
(3,  'EL-003', '4K UHD Monitor 27"',       'IPS display 60Hz with HDMI and DisplayPort',   1, 21500.00,  8, 1),
(4,  'OF-001', 'Multipurpose A4 Paper',    '80 GSM, 500 sheets per ream',                  2,   280.00, 50, 1),
(5,  'OF-002', 'Executive Gel Pen Pack',   'Box of 12 black gel rollerball pens',          2,   150.00, 30, 1),
(6,  'OF-003', 'Heavy-Duty Desktop Stapler','25-sheet capacity, anti-jam mechanism',       2,   320.00, 15, 1),
(7,  'FU-001', 'Ergonomic Mesh Chair',     'High-back lumbar support, 3D armrests',        3,  9500.00,  6, 1),
(8,  'FU-002', 'Adjustable Standing Desk', 'Motorized dual-motor height adjustable desk', 3, 24000.00,  4, 1),
(9,  'TL-001', 'Cordless Drill Kit 18V',   'Brushless motor with 2 Li-ion batteries',      4,  5200.00,  5, 1),
(10, 'CL-001', 'Disinfectant Sanitizer 5L','Hospital-grade surface disinfectant solution', 5,   650.00, 25, 1);

-- -----------------------------------------------------------------------------
-- 6. Initial Inventory Stock Counts (Warehouses 1 and 2)
-- Includes sample low-stock items for live dashboard demonstration:
--   - EL-001: 3 in Wh 1 (Reorder: 5) -> LOW STOCK ALERT ⚠️
--   - FU-002: 2 in Wh 2 (Reorder: 4) -> LOW STOCK ALERT ⚠️
--   - TL-001: 4 in Wh 1 (Reorder: 5) -> LOW STOCK ALERT ⚠️
-- -----------------------------------------------------------------------------
INSERT INTO inventory (product_id, warehouse_id, quantity) VALUES
(1,  1,   3),  -- EL-001 UltraBook Pro: 3 units (BELOW REORDER: 5) ⚠️
(2,  1,  65),  -- EL-002 Wireless Mouse: 65 units
(2,  2,  40),  -- EL-002 Wireless Mouse: 40 units in Wh 2
(3,  1,  18),  -- EL-003 4K Monitor: 18 units
(4,  1, 200),  -- OF-001 A4 Paper: 200 reams
(4,  2, 120),  -- OF-001 A4 Paper: 120 reams in Wh 2
(5,  1,  80),  -- OF-002 Gel Pens: 80 boxes
(6,  1,  25),  -- OF-003 Staplers: 25 units
(7,  1,  14),  -- FU-001 Mesh Chair: 14 units
(7,  2,   8),  -- FU-001 Mesh Chair: 8 units in Wh 2
(8,  2,   2),  -- FU-002 Standing Desk: 2 units (BELOW REORDER: 4) ⚠️
(9,  1,   4),  -- TL-001 Cordless Drill: 4 units (BELOW REORDER: 5) ⚠️
(10, 1,  50),  -- CL-001 Sanitizer: 50 cans
(10, 2,  35);  -- CL-001 Sanitizer: 35 cans in Wh 2
