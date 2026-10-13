package com.pms.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FXMLVerificationTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Platform already started, safe to ignore
        }
    }

    @Test
    @DisplayName("Verify LoginView.fxml exists and binds to LoginController")
    void testLoginView() throws Exception {
        URL url = getClass().getResource("/com/pms/ui/LoginView.fxml");
        assertNotNull(url, "LoginView.fxml must exist on classpath");

        FXMLLoader loader = new FXMLLoader(url);
        Object root = loader.load();
        assertNotNull(root);
        Object controller = loader.getController();
        assertNotNull(controller);
        assertTrue(controller instanceof LoginController, "Controller must be LoginController");
    }

    @Test
    @DisplayName("Verify MainDashboardView.fxml exists and binds to MainDashboardController")
    void testMainDashboardView() throws Exception {
        URL url = getClass().getResource("/com/pms/ui/MainDashboardView.fxml");
        assertNotNull(url, "MainDashboardView.fxml must exist on classpath");

        FXMLLoader loader = new FXMLLoader(url);
        Object root = loader.load();
        assertNotNull(root);
        Object controller = loader.getController();
        assertNotNull(controller);
        assertTrue(controller instanceof MainDashboardController, "Controller must be MainDashboardController");
    }

    @Test
    @DisplayName("Verify ProductView.fxml exists and binds to ProductController")
    void testProductView() throws Exception {
        URL url = getClass().getResource("/com/pms/ui/ProductView.fxml");
        assertNotNull(url, "ProductView.fxml must exist on classpath");

        FXMLLoader loader = new FXMLLoader(url);
        Object root = loader.load();
        assertNotNull(root);
        Object controller = loader.getController();
        assertNotNull(controller);
        assertTrue(controller instanceof ProductController, "Controller must be ProductController");
    }

    @Test
    @DisplayName("Verify InventoryView.fxml exists and binds to InventoryController")
    void testInventoryView() throws Exception {
        URL url = getClass().getResource("/com/pms/ui/InventoryView.fxml");
        assertNotNull(url, "InventoryView.fxml must exist on classpath");

        FXMLLoader loader = new FXMLLoader(url);
        Object root = loader.load();
        assertNotNull(root);
        Object controller = loader.getController();
        assertNotNull(controller);
        assertTrue(controller instanceof InventoryController, "Controller must be InventoryController");
    }

    @Test
    @DisplayName("Verify SalesView.fxml exists and binds to SalesController")
    void testSalesView() throws Exception {
        URL url = getClass().getResource("/com/pms/ui/SalesView.fxml");
        assertNotNull(url, "SalesView.fxml must exist on classpath");

        FXMLLoader loader = new FXMLLoader(url);
        Object root = loader.load();
        assertNotNull(root);
        Object controller = loader.getController();
        assertNotNull(controller);
        assertTrue(controller instanceof SalesController, "Controller must be SalesController");
    }
}
