package com.pms.dao;

import com.pms.model.Category;
import com.pms.model.Inventory;
import com.pms.model.Product;
import com.pms.model.PurchaseOrder;
import com.pms.model.PurchaseOrderItem;
import com.pms.model.Sale;
import com.pms.model.SaleItem;
import com.pms.model.StockMovement;
import com.pms.model.Supplier;
import com.pms.model.User;
import com.pms.model.Warehouse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DAOTest {

    @Test
    @DisplayName("UserDAO: Read seeded users and CRUD operations")
    void testUserDAO() {
        UserDAO dao = new UserDAO();
        List<User> users = dao.findAll();
        assertFalse(users.isEmpty(), "Should retrieve seeded users");

        Optional<User> adminOpt = dao.findByUsername("admin");
        assertTrue(adminOpt.isPresent(), "Admin user should exist");
        User admin = adminOpt.get();
        assertEquals("ADMIN", admin.getRole());
        assertEquals("admin", admin.getUsername());

        // Test insert and delete
        User newUser = new User("test_dao_user", "$2a$10$xyz", "Test User", "test@test.com", "WAREHOUSE_STAFF", true);
        int newId = dao.insert(newUser);
        assertTrue(newId > 0);

        Optional<User> fetched = dao.findById(newId);
        assertTrue(fetched.isPresent());
        assertEquals("Test User", fetched.get().getFullName());

        // Update
        fetched.get().setFullName("Updated Test User");
        assertTrue(dao.update(fetched.get()));

        // Delete
        assertTrue(dao.delete(newId));
    }

    @Test
    @DisplayName("CategoryDAO: Read seeded categories and CRUD operations")
    void testCategoryDAO() {
        CategoryDAO dao = new CategoryDAO();
        List<Category> categories = dao.findAll();
        assertEquals(5, categories.size(), "Should have 5 seeded categories");

        Optional<Category> cat = dao.findByName("Electronics");
        assertTrue(cat.isPresent());
        assertEquals("Electronics", cat.get().getName());

        // Insert, update, delete
        Category temp = new Category("TestCategoryTemp", "Temporary category description");
        int id = dao.insert(temp);
        assertTrue(id > 0);

        temp.setDescription("Updated description");
        assertTrue(dao.update(temp));

        assertTrue(dao.delete(id));
    }

    @Test
    @DisplayName("ProductDAO: Read seeded products, search and low-stock detection")
    void testProductDAO() {
        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.findAll();
        assertEquals(10, products.size(), "Should have 10 sample products");

        Optional<Product> p = dao.findBySku("EL-001");
        assertTrue(p.isPresent());
        assertTrue(p.get().getName().contains("UltraBook"));

        // Low stock products check (seeded 3 below reorder)
        List<Product> lowStock = dao.findLowStockProducts();
        assertFalse(lowStock.isEmpty(), "Should detect low stock products");

        // Search
        List<Product> searchResults = dao.search("UltraBook");
        assertFalse(searchResults.isEmpty());
        assertEquals("EL-001", searchResults.get(0).getSku());
    }

    @Test
    @DisplayName("SupplierDAO: Read seeded suppliers and CRUD operations")
    void testSupplierDAO() {
        SupplierDAO dao = new SupplierDAO();
        List<Supplier> suppliers = dao.findAll();
        assertEquals(4, suppliers.size(), "Should have 4 seeded suppliers");

        Optional<Supplier> s = dao.findById(1);
        assertTrue(s.isPresent());
        assertEquals("Global Tech Supplies", s.get().getName());

        // Insert, update, delete
        Supplier temp = new Supplier("Test Temp Supplier", "Tester", "1234567890", "test@sup.com", "Address", true);
        int id = dao.insert(temp);
        assertTrue(id > 0);

        temp.setContactPerson("New Tester");
        assertTrue(dao.update(temp));

        assertTrue(dao.delete(id));
    }

    @Test
    @DisplayName("WarehouseDAO: Read seeded warehouses and CRUD operations")
    void testWarehouseDAO() {
        WarehouseDAO dao = new WarehouseDAO();
        List<Warehouse> warehouses = dao.findAll();
        assertEquals(2, warehouses.size(), "Should have 2 seeded warehouses");

        Optional<Warehouse> wh = dao.findByName("Main Central Warehouse");
        assertTrue(wh.isPresent());
        assertEquals(15000, wh.get().getCapacity());

        // Insert, update, delete
        Warehouse temp = new Warehouse("Temp Warehouse", "Test Loc", 500, true);
        int id = dao.insert(temp);
        assertTrue(id > 0);

        temp.setLocation("Updated Loc");
        assertTrue(dao.update(temp));

        assertTrue(dao.delete(id));
    }

    @Test
    @DisplayName("InventoryDAO: Read seeded inventory and query totals")
    void testInventoryDAO() {
        InventoryDAO dao = new InventoryDAO();
        List<Inventory> all = dao.findAll();
        assertEquals(14, all.size(), "Should have 14 seeded inventory rows");

        // Product 1 (UltraBook) at warehouse 1 has qty 3
        Optional<Inventory> inv = dao.findByProductAndWarehouse(1, 1);
        assertTrue(inv.isPresent());
        assertEquals(3, inv.get().getQuantity());

        int totalP1 = dao.getTotalQuantityByProduct(1);
        assertEquals(3, totalP1);
    }

    @Test
    @DisplayName("PurchaseOrderDAO: Insert PO with items and query")
    void testPurchaseOrderDAO() {
        PurchaseOrderDAO dao = new PurchaseOrderDAO();
        PurchaseOrder po = new PurchaseOrder();
        po.setSupplierId(1);
        po.setWarehouseId(1);
        po.setStatus("PENDING");
        po.setOrderDate(LocalDate.now());
        po.setNotes("Test Purchase Order");
        po.setCreatedBy(1);

        po.getItems().add(new PurchaseOrderItem(1, 10, new BigDecimal("45000.00")));
        po.getItems().add(new PurchaseOrderItem(2, 50, new BigDecimal("850.00")));

        int poId = dao.insert(po);
        assertTrue(poId > 0);

        Optional<PurchaseOrder> fetched = dao.findById(poId);
        assertTrue(fetched.isPresent());
        assertEquals(2, fetched.get().getItems().size());
        assertEquals("PENDING", fetched.get().getStatus());

        // Update status to APPROVED
        assertTrue(dao.updateStatus(poId, "APPROVED", 1));

        // Delete test PO
        assertTrue(dao.delete(poId));
    }

    @Test
    @DisplayName("SaleDAO: Insert Sale with items and query by invoice")
    void testSaleDAO() {
        SaleDAO dao = new SaleDAO();
        Sale sale = new Sale();
        String invNum = "INV-TEST-" + System.currentTimeMillis();
        sale.setInvoiceNumber(invNum);
        sale.setWarehouseId(1);
        sale.setCustomerName("Test Customer");
        sale.setCustomerPhone("9999999999");
        sale.setTotalAmount(new BigDecimal("90000.00"));
        sale.setCreatedBy(1);

        sale.getItems().add(new SaleItem(1, 2, new BigDecimal("45000.00")));

        int saleId = dao.insert(sale);
        assertTrue(saleId > 0);

        Optional<Sale> fetched = dao.findByInvoiceNumber(invNum);
        assertTrue(fetched.isPresent());
        assertEquals("Test Customer", fetched.get().getCustomerName());
        assertEquals(1, fetched.get().getItems().size());

        // Cleanup
        assertTrue(dao.delete(saleId));
    }

    @Test
    @DisplayName("StockMovementDAO: Insert stock movement audit log and query")
    void testStockMovementDAO() {
        StockMovementDAO dao = new StockMovementDAO();
        StockMovement sm = new StockMovement(1, 1, null, "IN", 10, "Test Inward Movement", 1);
        int smId = dao.insert(sm);
        assertTrue(smId > 0);

        Optional<StockMovement> fetched = dao.findById(smId);
        assertTrue(fetched.isPresent());
        assertEquals("IN", fetched.get().getMovementType());
        assertEquals(10, fetched.get().getQuantity());

        // Cleanup
        assertTrue(dao.delete(smId));
    }
}
