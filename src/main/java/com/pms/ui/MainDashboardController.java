package com.pms.ui;

import com.ims.common.util.AlertUtil;
import com.pms.dao.ProductDAO;
import com.pms.dao.SupplierDAO;
import com.pms.dao.WarehouseDAO;
import com.pms.model.Product;
import com.pms.model.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the Main Dashboard view, navigation sidebar, and inventory metrics/alerts.
 */
public class MainDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(MainDashboardController.class);

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label userWelcomeLabel;

    @FXML
    private Label userRoleBadge;

    // Sidebar navigation buttons
    @FXML
    private Button navDashboardButton;

    @FXML
    private Button navProductsButton;

    @FXML
    private Button navInventoryButton;

    @FXML
    private Button navSalesButton;

    @FXML
    private Button navUsersButton;

    @FXML
    private Button navLogoutButton;

    // Metric Summary Cards
    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label lowStockAlertsLabel;

    @FXML
    private Label totalWarehousesLabel;

    @FXML
    private Label totalSuppliersLabel;

    // Low stock TableView
    @FXML
    private TableView<Product> lowStockTable;

    @FXML
    private TableColumn<Product, String> lowStockSkuCol;

    @FXML
    private TableColumn<Product, String> lowStockNameCol;

    @FXML
    private TableColumn<Product, Number> lowStockReorderCol;

    // Content container for subviews
    @FXML
    private StackPane contentArea;

    @FXML
    private VBox dashboardView;

    private final ProductDAO productDAO = new ProductDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    private Runnable onLogoutCallback;

    public void setOnLogoutCallback(Runnable callback) {
        this.onLogoutCallback = callback;
    }

    @FXML
    public void initialize() {
        setupUserInfo();
        applyRolePermissions();
        setupLowStockTable();
        refreshMetrics();
    }

    /**
     * Updates header with active user details.
     */
    public void setupUserInfo() {
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            if (userWelcomeLabel != null) {
                userWelcomeLabel.setText("Welcome, " + user.getFullName());
            }
            if (userRoleBadge != null) {
                userRoleBadge.setText(user.getRole());
            }
        }
    }

    /**
     * Configures sidebar button visibility and permissions according to the role matrix.
     */
    public void applyRolePermissions() {
        User user = UserSession.getInstance().getCurrentUser();
        if (user == null) return;

        String role = user.getRole();

        // User Management is strictly ADMIN only
        if (navUsersButton != null) {
            boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
            navUsersButton.setVisible(isAdmin);
            navUsersButton.setManaged(isAdmin);
        }

        // Sales staff restrictions
        if (navInventoryButton != null) {
            boolean canAccessInventory = !"SALES_STAFF".equalsIgnoreCase(role);
            navInventoryButton.setVisible(canAccessInventory);
            navInventoryButton.setManaged(canAccessInventory);
        }

        // Warehouse staff restrictions
        if (navSalesButton != null) {
            boolean canAccessSales = !"WAREHOUSE_STAFF".equalsIgnoreCase(role);
            navSalesButton.setVisible(canAccessSales);
            navSalesButton.setManaged(canAccessSales);
        }
    }

    private void setupLowStockTable() {
        if (lowStockTable == null) return;

        if (lowStockSkuCol != null) {
            lowStockSkuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSku()));
        }
        if (lowStockNameCol != null) {
            lowStockNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        }
        if (lowStockReorderCol != null) {
            lowStockReorderCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getReorderLevel()));
        }
    }

    /**
     * Refreshes summary cards and low stock alerts from the database.
     */
    public void refreshMetrics() {
        try {
            int activeProducts = productDAO.findAllActive().size();
            List<Product> lowStockProducts = productDAO.findLowStockProducts();
            int activeWarehouses = warehouseDAO.findAllActive().size();
            int activeSuppliers = supplierDAO.findAllActive().size();

            if (totalProductsLabel != null) {
                totalProductsLabel.setText(String.valueOf(activeProducts));
            }
            if (lowStockAlertsLabel != null) {
                lowStockAlertsLabel.setText(String.valueOf(lowStockProducts.size()));
            }
            if (totalWarehousesLabel != null) {
                totalWarehousesLabel.setText(String.valueOf(activeWarehouses));
            }
            if (totalSuppliersLabel != null) {
                totalSuppliersLabel.setText(String.valueOf(activeSuppliers));
            }

            if (lowStockTable != null) {
                lowStockTable.setItems(FXCollections.observableArrayList(lowStockProducts));
            }
        } catch (Exception e) {
            logger.error("Failed to load dashboard metrics", e);
        }
    }

    @FXML
    public void handleNavDashboard(ActionEvent event) {
        showView(dashboardView);
        refreshMetrics();
    }

    @FXML
    public void handleNavProducts(ActionEvent event) {
        com.pms.ProductManagementApp.showProductsView();
    }

    @FXML
    public void handleNavInventory(ActionEvent event) {
        com.pms.ProductManagementApp.showInventoryView();
    }

    @FXML
    public void handleNavSales(ActionEvent event) {
        com.pms.ProductManagementApp.showSalesView();
    }

    @FXML
    public void handleNavUsers(ActionEvent event) {
        AlertUtil.showInfo("User Management", "User management is reserved for System Administrators.");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        Optional<ButtonType> confirm = AlertUtil.showConfirm("Logout", "Are you sure you want to log out?");
        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            UserSession.getInstance().clear();
            logger.info("User logged out");
            if (onLogoutCallback != null) {
                onLogoutCallback.run();
            }
        }
    }

    /**
     * Displays a view inside the main content area.
     *
     * @param viewNode the view to display
     */
    public void showView(Node viewNode) {
        if (contentArea != null && viewNode != null) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(viewNode);
        }
    }

    public StackPane getContentArea() {
        return contentArea;
    }
}
