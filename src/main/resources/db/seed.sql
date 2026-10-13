-- =============================================================================
-- Inventory Management System — Seed Data
-- BCrypt hashes generated at cost factor 10 (jbcrypt-compatible $2a$ prefix).
-- Passwords: admin→admin123, manager→manager123,
--            warehouse→staff123, sales→sales123
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Users  (BCrypt hashes — real, verifiable)
-- admin123     → $2b$10$gsFUhTupQe05Els86ps24eujanVsC9D73UUFkTDOjBoAnD/6kKrsa
-- manager123   → $2b$10$LM2i0Vvr5FbZXmvBOgnMqexpXj4Z5sgEXHH7xYEW6iM3KF7ACWQpe
-- staff123     → $2b$10$JeMOGQp1lPn5lMWbA89pw.Rq9h/J3S8cYMb/QfxJi/bcqfEc4n2eu
-- sales123     → $2b$10$fRmd5a26WYxW9AQZyb6l3OroWrQT6/DwsdOyB.yeyi1BjfaSYCh66
-- NOTE: jbcrypt uses $2a$ prefix; replace leading $2b$ with $2a$ for compatibility
-- -----------------------------------------------------------------------------
INSERT INTO users (username, password_hash, full_name, email, role, active) VALUES
('admin',
 '$2a$10$gsFUhTupQe05Els86ps24eujanVsC9D73UUFkTDOjBoAnD/6kKrsa',
 'System Administrator', 'admin@ims.local', 'ADMIN', 1),
('manager',
 '$2a$10$LM2i0Vvr5FbZXmvBOgnMqexpXj4Z5sgEXHH7xYEW6iM3KF7ACWQpe',
 'Inventory Manager', 'manager@ims.local', 'MANAGER', 1),
('warehouse',
 '$2a$10$JeMOGQp1lPn5lMWbA89pw.Rq9h/J3S8cYMb/QfxJi/bcqfEc4n2eu',
 'Warehouse Staff', 'warehouse@ims.local', 'WAREHOUSE_STAFF', 1),
('sales',
 '$2a$10$fRmd5a26WYxW9AQZyb6l3OroWrQT6/DwsdOyB.yeyi1BjfaSYCh66',
 'Sales Associate', 'sales@ims.local', 'SALES_STAFF', 1);

-- -----------------------------------------------------------------------------
-- Categories (5)
-- -----------------------------------------------------------------------------
INSERT INTO categories (name, description) VALUES
('Electronics',     'Electronic components, devices and accessories'),
('Office Supplies',  'Stationery, paper, pens and office consumables'),
('Furniture',        'Desks, chairs, shelves and office furniture'),
('Cleaning Supplies','Detergents, mops, sanitisers and janitorial items'),
('Tools & Hardware', 'Hand tools, power tools and fasteners');

-- -----------------------------------------------------------------------------
-- Suppliers (6)
-- -----------------------------------------------------------------------------
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('TechSource Pvt Ltd',    'Ravi Kumar',    '9800001111', 'ravi@techsource.in',    '12 MG Road, Bangalore'),
('OfficeWorld Supplies',  'Priya Sharma',  '9800002222', 'priya@officeworld.in',  '5 Park Street, Chennai'),
('FurniPlus Co.',         'Anil Mehta',    '9800003333', 'anil@furniplus.in',     '88 Industrial Area, Pune'),
('CleanPro Distributors', 'Sunita Rao',    '9800004444', 'sunita@cleanpro.in',    '3 Whitefield, Hyderabad'),
('Hardware Hub',          'Manoj Tiwari',  '9800005555', 'manoj@hardwarehub.in',  '21 Karol Bagh, Delhi'),
('Global Electronics Ltd','Deepa Nair',    '9800006666', 'deepa@globalelec.in',   '7 Andheri East, Mumbai');

-- -----------------------------------------------------------------------------
-- Warehouses (3)
-- -----------------------------------------------------------------------------
INSERT INTO warehouses (name, location, capacity) VALUES
('Main Warehouse',   'Plot 1, Industrial Estate, Chennai',  10000),
('North Hub',        'Sector 14, Gurgaon, Delhi NCR',        5000),
('South Depot',      'KIADB Industrial Area, Bangalore',      7500);

-- -----------------------------------------------------------------------------
-- Products (25)
-- category_id: 1=Electronics 2=Office Supplies 3=Furniture 4=Cleaning 5=Tools
-- -----------------------------------------------------------------------------
INSERT INTO products (sku, name, description, category_id, unit_price, reorder_level) VALUES
-- Electronics (1)
('EL-001', 'Laptop 15"',           'Core i5, 8GB RAM, 512GB SSD',          1, 45000.00, 5),
('EL-002', 'Wireless Mouse',       'Ergonomic 2.4GHz wireless mouse',       1,   850.00, 20),
('EL-003', 'USB Hub 4-Port',       'USB 3.0 powered hub',                   1,   650.00, 30),
('EL-004', 'LED Monitor 24"',      'Full HD IPS display',                   1, 12500.00, 8),
('EL-005', 'Mechanical Keyboard',  'TKL layout, Blue switches',             1,  3200.00, 10),
('EL-006', 'Webcam HD 1080p',      'Auto-focus, built-in mic',              1,  2100.00, 15),
-- Office Supplies (2)
('OF-001', 'A4 Paper Ream',        '500 sheets, 75 GSM',                    2,   250.00, 100),
('OF-002', 'Ball Pen Box',         'Box of 10 blue ball pens',              2,    80.00, 50),
('OF-003', 'Stapler',              'Heavy duty 26/6 stapler',               2,   220.00, 20),
('OF-004', 'Sticky Notes Pack',    '5 colours, 100 sheets each',            2,   130.00, 40),
('OF-005', 'Whiteboard Marker Set','Pack of 8 assorted colours',            2,   180.00, 30),
-- Furniture (3)
('FU-001', 'Ergonomic Office Chair','Mesh back, lumbar support',            3,  8500.00, 5),
('FU-002', 'Executive Desk',        '1.5m, with cable management',          3, 14000.00, 3),
('FU-003', 'Steel Bookshelf',       '5 tier, 180cm height',                 3,  4200.00, 5),
('FU-004', 'Filing Cabinet 3-Draw','Steel, lockable',                       3,  5800.00, 4),
-- Cleaning Supplies (4)
('CL-001', 'Floor Cleaner 5L',     'Phenyl-based disinfectant',             4,   420.00, 20),
('CL-002', 'Hand Sanitiser 500ml', '70% isopropyl alcohol',                 4,   180.00, 50),
('CL-003', 'Mop with Bucket',      'Spin mop set, 360° rotation',           4,   750.00, 10),
('CL-004', 'Dustbin 30L',          'Pedal-operated stainless steel',        4,   650.00, 8),
-- Tools & Hardware (5)
('TL-001', 'Cordless Drill',       '18V, 2 batteries included',             5,  4800.00, 5),
('TL-002', 'Screwdriver Set 12pc', 'Phillips & flat, chrome-vanadium',      5,   580.00, 15),
('TL-003', 'Measuring Tape 5m',    'Auto-lock, magnetic tip',               5,   220.00, 25),
('TL-004', 'Safety Helmet',        'ISI marked, adjustable',                5,   390.00, 20),
('TL-005', 'Work Gloves Pair',     'Leather palm, cut-resistant',           5,   260.00, 30),
('TL-006', 'Padlock 50mm',         'Hardened steel shackle',                5,   340.00, 20);

-- -----------------------------------------------------------------------------
-- Inventory — opening stock (product_id × warehouse_id)
-- Products deliberately below reorder_level: EL-001(5), FU-002(3), TL-001(5)
-- Below reorder: EL-001 qty=3 (reorder=5), FU-002 qty=1 (reorder=3), TL-001 qty=2 (reorder=5)
-- -----------------------------------------------------------------------------
INSERT INTO inventory (product_id, warehouse_id, quantity) VALUES
-- Electronics in Main Warehouse (wh 1)
(1,  1,  3),   -- EL-001 Laptop       qty=3  BELOW reorder=5  ⚠
(2,  1, 45),   -- EL-002 Mouse        qty=45
(3,  1, 60),   -- EL-003 USB Hub      qty=60
(4,  1, 12),   -- EL-004 Monitor      qty=12
(5,  1, 20),   -- EL-005 Keyboard     qty=20
(6,  1, 18),   -- EL-006 Webcam       qty=18
-- Office Supplies spread across Main + North
(7,  1, 200),  -- OF-001 Paper        Main
(7,  2, 150),  -- OF-001 Paper        North
(8,  1,  80),  -- OF-002 Pens         Main
(9,  1,  35),  -- OF-003 Stapler      Main
(10, 1,  90),  -- OF-004 Sticky Notes Main
(11, 1,  55),  -- OF-005 Markers      Main
-- Furniture in South Depot (wh 3) + Main
(12, 3,  10),  -- FU-001 Chair        South
(13, 3,   1),  -- FU-002 Desk         qty=1  BELOW reorder=3  ⚠
(14, 3,   8),  -- FU-003 Bookshelf    South
(15, 3,   6),  -- FU-004 Cabinet      South
-- Cleaning in all 3 warehouses
(16, 1,  40),  -- CL-001 Floor Cleaner Main
(17, 1,  80),  -- CL-002 Sanitiser    Main
(17, 2,  60),  -- CL-002 Sanitiser    North
(18, 1,  15),  -- CL-003 Mop          Main
(19, 1,  12),  -- CL-004 Dustbin      Main
-- Tools in North + Main
(20, 2,   2),  -- TL-001 Drill        qty=2  BELOW reorder=5  ⚠
(21, 2,  30),  -- TL-002 Screwdriver  North
(22, 2,  50),  -- TL-003 Tape         North
(23, 2,  40),  -- TL-004 Helmet       North
(24, 2,  60),  -- TL-005 Gloves       North
(25, 2,  35);  -- TL-006 Padlock      North
