package com.pms;

import com.ims.common.db.DatabaseManager;
import com.ims.common.util.AlertUtil;
import com.pms.ui.LoginController;
import com.pms.ui.MainDashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Primary JavaFX Application entry point for the Product &amp; Inventory Management System.
 * Manages stage configuration and provides scene-switching helper methods across views.
 */
public class ProductManagementApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(ProductManagementApp.class);

    private static Stage primaryStage;
    private static MainDashboardController dashboardController;

    private static final double APP_WIDTH = 1200;
    private static final double APP_HEIGHT = 700;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(600);

        // Ensure database pool is gracefully closed on exit
        primaryStage.setOnCloseRequest(e -> {
            logger.info("Application closing. Shutting down database pool.");
            DatabaseManager.shutdown();
        });

        showLoginView();
    }

    /**
     * Switches the primary stage to display LoginView.fxml.
     */
    public static void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(ProductManagementApp.class.getResource("/com/pms/ui/LoginView.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setOnLoginSuccessCallback(user -> showMainDashboardView());

            Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
            logger.info("Login view loaded.");
        } catch (IOException e) {
            logger.error("Failed to load LoginView.fxml", e);
            AlertUtil.showError("Startup Error", "Could not load Login view: " + e.getMessage());
        }
    }

    /**
     * Switches the primary stage to display MainDashboardView.fxml upon authentication.
     */
    public static void showMainDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(ProductManagementApp.class.getResource("/com/pms/ui/MainDashboardView.fxml"));
            Parent root = loader.load();

            dashboardController = loader.getController();
            dashboardController.setOnLogoutCallback(ProductManagementApp::showLoginView);

            Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
            logger.info("Main dashboard view loaded.");
        } catch (IOException e) {
            logger.error("Failed to load MainDashboardView.fxml", e);
            AlertUtil.showError("Navigation Error", "Could not load Main Dashboard: " + e.getMessage());
        }
    }

    /**
     * Transitions to the Products screen inside the main dashboard content area.
     */
    public static void showProductsView() {
        loadViewIntoDashboard("/com/pms/ui/ProductView.fxml");
    }

    /**
     * Transitions to the Inventory screen inside the main dashboard content area.
     */
    public static void showInventoryView() {
        loadViewIntoDashboard("/com/pms/ui/InventoryView.fxml");
    }

    /**
     * Transitions to the Sales (POS) screen inside the main dashboard content area.
     */
    public static void showSalesView() {
        loadViewIntoDashboard("/com/pms/ui/SalesView.fxml");
    }

    /**
     * Transitions back to the Home Dashboard view.
     */
    public static void showDashboardView() {
        if (dashboardController != null) {
            dashboardController.handleNavDashboard(null);
        }
    }

    /**
     * Helper method to load an FXML node into the Main Dashboard's content area.
     *
     * @param fxmlPath path to the FXML file
     */
    public static void loadViewIntoDashboard(String fxmlPath) {
        if (dashboardController == null || dashboardController.getContentArea() == null) {
            logger.warn("Dashboard controller or content area not initialized. Loading main dashboard first.");
            showMainDashboardView();
        }

        try {
            FXMLLoader loader = new FXMLLoader(ProductManagementApp.class.getResource(fxmlPath));
            Node node = loader.load();
            if (dashboardController != null) {
                dashboardController.showView(node);
                logger.info("Successfully navigated to: {}", fxmlPath);
            }
        } catch (IOException e) {
            logger.error("Failed to load view from: {}", fxmlPath, e);
            AlertUtil.showError("View Error", "Could not load view: " + fxmlPath + " (" + e.getMessage() + ")");
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static MainDashboardController getDashboardController() {
        return dashboardController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
