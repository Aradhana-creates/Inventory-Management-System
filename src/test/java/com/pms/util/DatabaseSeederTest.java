package com.pms.util;

import com.pms.dao.ProductDAO;
import com.pms.dao.UserDAO;
import com.pms.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseSeederTest {

    @Test
    @DisplayName("Verify DatabaseSeeder executes and populates verifiable BCrypt users and products")
    void testSeederExecution() {
        // Run seed
        DatabaseSeeder.runSeed();

        // Verify users
        UserDAO userDAO = new UserDAO();
        Optional<User> admin = userDAO.findByUsername("admin");
        assertTrue(admin.isPresent(), "Admin must exist");
        assertTrue(BCrypt.checkpw("admin123", admin.get().getPasswordHash()), "Admin password must verify");

        Optional<User> manager = userDAO.findByUsername("manager");
        assertTrue(manager.isPresent(), "Manager must exist");
        assertTrue(BCrypt.checkpw("manager123", manager.get().getPasswordHash()), "Manager password must verify");

        Optional<User> sales = userDAO.findByUsername("sales");
        assertTrue(sales.isPresent(), "Sales staff must exist");
        assertTrue(BCrypt.checkpw("sales123", sales.get().getPasswordHash()), "Sales password must verify");

        // Verify products count is 10
        ProductDAO productDAO = new ProductDAO();
        assertEquals(10, productDAO.findAll().size(), "Should have exactly 10 products");
    }
}
