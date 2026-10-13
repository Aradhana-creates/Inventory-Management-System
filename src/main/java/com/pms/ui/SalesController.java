package com.pms.ui;

import com.ims.common.db.DatabaseManager;
import com.ims.common.util.AlertUtil;
import com.ims.common.util.Validator;
import com.pms.dao.InventoryDAO;
import com.pms.dao.ProductDAO;
import com.pms.dao.SaleDAO;
import com.pms.dao.StockMovementDAO;
import com.pms.dao.WarehouseDAO;
import com.pms.model.Inventory;
import com.pms.model.Product;
import com.pms.model.Sale;
import com.pms.model.SaleItem;
import com.pms.model.StockMovement;
import com.pms.model.User;
import com.pms.model.Warehouse;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for POS checkout sales interface. Handles item entry, cart calculations,
 * and creating sale records and stock reductions transactionally.
 */
public class SalesController {

    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);

    @FXML
    private ComboBox<Warehouse> warehouseComboBox;

    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private Label availableStockLabel;

    @FXML
    private TextField unitPriceField;

    @FXML
    private TextField quantityField;

    @FXML
    private Button addToCartButton;

    // Customer details
    @FXML
    private TextField customerNameField;

    @FXML
    private TextField customerPhoneField;

    // Cart Table
    @FXML
    private TableView<CartItemRow> cartTable;

    @FXML
    private TableColumn<CartItemRow, String> cartSkuCol;

    @FXML
    private TableColumn<CartItemRow, String> cartNameCol;

    @FXML
    private TableColumn<CartItemRow, BigDecimal> cartPriceCol;

    @FXML
    private TableColumn<CartItemRow, Number> cartQtyCol;

    @FXML
    private TableColumn<CartItemRow, BigDecimal> cartSubtotalCol;

    @FXML
    private Button removeCartItemButton;

    @FXML
    private Button clearCartButton;

    // Checkout
    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button checkoutButton;

    private final ProductDAO productDAO = new ProductDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final StockMovementDAO stockMovementDAO = new StockMovementDAO();

    private final ObservableList<CartItemRow> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupCartTable();
        loadWarehouses();
        loadProducts();
        setupListeners();
        updateTotal();
    }

    private void setupCartTable() {
        if (cartSkuCol != null) {
            cartSkuCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSku()));
        }
        if (cartNameCol != null) {
            cartNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductName()));
        }
        if (cartPriceCol != null) {
            cartPriceCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getUnitPrice()));
        }
        if (cartQtyCol != null) {
            cartQtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()));
        }
        if (cartSubtotalCol != null) {
            cartSubtotalCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getSubtotal()));
        }
        if (cartTable != null) {
            cartTable.setItems(cartItems);
        }
    }

    private void loadWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseDAO.findAllActive();
            if (warehouseComboBox != null) {
                warehouseComboBox.setItems(FXCollections.observableArrayList(warehouses));
                warehouseComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Warehouse w) { return w != null ? w.getName() : ""; }
                    @Override
                    public Warehouse fromString(String s) { return null; }
                });
                if (!warehouses.isEmpty()) {
                    warehouseComboBox.setValue(warehouses.get(0));
                }
            }
        } catch (Exception e) {
            logger.error("Error loading warehouses for sales", e);
        }
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.findAllActive();
            if (productComboBox != null) {
                productComboBox.setItems(FXCollections.observableArrayList(products));
                productComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Product p) { return p != null ? p.getSku() + " - " + p.getName() : ""; }
                    @Override
                    public Product fromString(String s) { return null; }
                });
            }
        } catch (Exception e) {
            logger.error("Error loading products for sales", e);
        }
    }

    private void setupListeners() {
        if (productComboBox != null) {
            productComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if (unitPriceField != null) {
                        unitPriceField.setText(newVal.getUnitPrice() != null ? newVal.getUnitPrice().toString() : "0.00");
                    }
                    updateAvailableStock();
                }
            });
        }

        if (warehouseComboBox != null) {
            warehouseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateAvailableStock();
            });
        }
    }

    private void updateAvailableStock() {
        Product p = productComboBox != null ? productComboBox.getValue() : null;
        Warehouse w = warehouseComboBox != null ? warehouseComboBox.getValue() : null;
        if (p != null && w != null && availableStockLabel != null) {
            try {
                Optional<Inventory> inv = inventoryDAO.findByProductAndWarehouse(p.getId(), w.getId());
                int available = inv.map(Inventory::getQuantity).orElse(0);
                availableStockLabel.setText("Available: " + available);
            } catch (Exception e) {
                availableStockLabel.setText("Available: 0");
            }
        }
    }

    @FXML
    public void handleAddToCart(ActionEvent event) {
        Product product = productComboBox != null ? productComboBox.getValue() : null;
        Warehouse warehouse = warehouseComboBox != null ? warehouseComboBox.getValue() : null;
        String qtyStr = quantityField != null ? quantityField.getText().trim() : "";
        String priceStr = unitPriceField != null ? unitPriceField.getText().trim() : "";

        if (warehouse == null || product == null) {
            AlertUtil.showWarning("Validation", "Please select both a warehouse and a product.");
            return;
        }

        if (!Validator.isPositiveInt(qtyStr)) {
            AlertUtil.showWarning("Validation", "Quantity must be a positive whole number.");
            return;
        }

        if (!Validator.isNonNegativeDecimal(priceStr)) {
            AlertUtil.showWarning("Validation", "Unit price must be a valid non-negative decimal.");
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        BigDecimal price = new BigDecimal(priceStr).setScale(2, RoundingMode.HALF_UP);

        // Check stock availability
        Optional<Inventory> invOpt = inventoryDAO.findByProductAndWarehouse(product.getId(), warehouse.getId());
        int currentStock = invOpt.map(Inventory::getQuantity).orElse(0);

        // Check if item is already in cart
        int existingCartQty = 0;
        for (CartItemRow row : cartItems) {
            if (row.getProductId() == product.getId()) {
                existingCartQty += row.getQuantity();
            }
        }

        if (existingCartQty + quantity > currentStock) {
            AlertUtil.showError("Insufficient Stock",
                    String.format("Available stock in %s is %d. Total requested in cart would be %d. Cannot oversell.",
                            warehouse.getName(), currentStock, existingCartQty + quantity));
            return;
        }

        // Add or merge into cart
        boolean found = false;
        for (CartItemRow row : cartItems) {
            if (row.getProductId() == product.getId()) {
                row.setQuantity(row.getQuantity() + quantity);
                row.setSubtotal(row.getUnitPrice().multiply(BigDecimal.valueOf(row.getQuantity())));
                found = true;
                break;
            }
        }

        if (!found) {
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            cartItems.add(new CartItemRow(product.getId(), product.getSku(), product.getName(), price, quantity, subtotal));
        }

        if (cartTable != null) cartTable.refresh();
        updateTotal();

        if (quantityField != null) quantityField.clear();
    }

    @FXML
    public void handleRemoveCartItem(ActionEvent event) {
        if (cartTable != null) {
            CartItemRow selected = cartTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cartItems.remove(selected);
                updateTotal();
            } else {
                AlertUtil.showWarning("Selection", "Please select an item from the cart to remove.");
            }
        }
    }

    @FXML
    public void handleClearCart(ActionEvent event) {
        cartItems.clear();
        updateTotal();
    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemRow item : cartItems) {
            total = total.add(item.getSubtotal());
        }
        if (totalAmountLabel != null) {
            totalAmountLabel.setText("₹" + total.setScale(2, RoundingMode.HALF_UP).toString());
        }
    }

    @FXML
    public void handleCheckout(ActionEvent event) {
        if (cartItems.isEmpty()) {
            AlertUtil.showWarning("Cart Empty", "Please add at least one product to the cart before checkout.");
            return;
        }

        Warehouse warehouse = warehouseComboBox != null ? warehouseComboBox.getValue() : null;
        if (warehouse == null) {
            AlertUtil.showWarning("Validation", "Please select a warehouse for this sale.");
            return;
        }

        String customerName = customerNameField != null ? customerNameField.getText().trim() : "Walk-in Customer";
        String customerPhone = customerPhoneField != null ? customerPhoneField.getText().trim() : "";

        User currentUser = UserSession.getInstance().getCurrentUser();
        int userId = currentUser != null ? currentUser.getId() : 1;

        String invoiceNumber = "INV-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemRow item : cartItems) {
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // Transaction execution
        try (Connection conn = DatabaseManager.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                // Verify all cart items against live stock
                for (CartItemRow item : cartItems) {
                    Optional<Inventory> inv = inventoryDAO.findByProductAndWarehouse(conn, item.getProductId(), warehouse.getId());
                    int stock = inv.map(Inventory::getQuantity).orElse(0);
                    if (stock < item.getQuantity()) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        AlertUtil.showError("Stock Underflow",
                                "Insufficient stock for " + item.getProductName() + ". Available: " + stock + ", required: " + item.getQuantity());
                        return;
                    }
                }

                // Create Sale entity
                Sale sale = new Sale();
                sale.setInvoiceNumber(invoiceNumber);
                sale.setWarehouseId(warehouse.getId());
                sale.setCustomerName(customerName);
                sale.setCustomerPhone(customerPhone);
                sale.setTotalAmount(totalAmount);
                sale.setCreatedBy(userId);

                for (CartItemRow row : cartItems) {
                    sale.getItems().add(new SaleItem(row.getProductId(), row.getQuantity(), row.getUnitPrice()));
                }

                // Insert sale header & sale items
                saleDAO.insert(sale);

                // Reduce inventory and log movement OUT
                for (CartItemRow row : cartItems) {
                    Optional<Inventory> inv = inventoryDAO.findByProductAndWarehouse(conn, row.getProductId(), warehouse.getId());
                    int currentStock = inv.map(Inventory::getQuantity).orElse(0);
                    int newStock = currentStock - row.getQuantity();

                    inventoryDAO.updateQuantity(conn, row.getProductId(), warehouse.getId(), newStock);

                    StockMovement sm = new StockMovement();
                    sm.setProductId(row.getProductId());
                    sm.setWarehouseId(warehouse.getId());
                    sm.setMovementType("OUT");
                    sm.setQuantity(row.getQuantity());
                    sm.setReferenceNote(invoiceNumber);
                    sm.setPerformedBy(userId);
                    stockMovementDAO.insert(conn, sm);
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);

                AlertUtil.showInfo("Sale Completed",
                        String.format("Invoice generated: %s\nTotal Amount: ₹%.2f\nThank you!",
                                invoiceNumber, totalAmount));

                // Reset POS
                cartItems.clear();
                updateTotal();
                if (customerNameField != null) customerNameField.clear();
                if (customerPhoneField != null) customerPhoneField.clear();
                updateAvailableStock();

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (Exception e) {
            logger.error("Checkout transaction failed", e);
            AlertUtil.showError("Checkout Error", "Failed to complete transaction: " + e.getMessage());
        }
    }

    /**
     * Cart Item row representation for TableView.
     */
    public static class CartItemRow {
        private final int productId;
        private final String sku;
        private final String productName;
        private final BigDecimal unitPrice;
        private int quantity;
        private BigDecimal subtotal;

        public CartItemRow(int productId, String sku, String productName, BigDecimal unitPrice, int quantity, BigDecimal subtotal) {
            this.productId = productId;
            this.sku = sku;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }

        public int getProductId() { return productId; }
        public String getSku() { return sku; }
        public String getProductName() { return productName; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
}
