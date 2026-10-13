package com.pms.ui;

import com.ims.common.db.DatabaseManager;
import com.ims.common.util.AlertUtil;
import com.ims.common.util.Validator;
import com.pms.dao.InventoryDAO;
import com.pms.dao.ProductDAO;
import com.pms.dao.StockMovementDAO;
import com.pms.dao.WarehouseDAO;
import com.pms.model.Inventory;
import com.pms.model.Product;
import com.pms.model.StockMovement;
import com.pms.model.User;
import com.pms.model.Warehouse;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for Inventory Management and Stock Movements.
 * Handles updating stock counts in a transactional manner and logging entries to 'stock_movements'.
 */
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Inventory Table
    @FXML
    private TableView<InventoryDisplayRow> inventoryTable;

    @FXML
    private TableColumn<InventoryDisplayRow, String> invSkuCol;

    @FXML
    private TableColumn<InventoryDisplayRow, String> invProductNameCol;

    @FXML
    private TableColumn<InventoryDisplayRow, String> invWarehouseCol;

    @FXML
    private TableColumn<InventoryDisplayRow, Number> invQtyCol;

    @FXML
    private TableColumn<InventoryDisplayRow, String> invStatusCol;

    // Movement Form Controls
    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private ComboBox<Warehouse> sourceWarehouseComboBox;

    @FXML
    private ComboBox<String> movementTypeComboBox;

    @FXML
    private ComboBox<Warehouse> destWarehouseComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField referenceNoteField;

    @FXML
    private Button recordMovementButton;

    // Movement History Table
    @FXML
    private TableView<StockMovement> movementTable;

    @FXML
    private TableColumn<StockMovement, String> moveDateCol;

    @FXML
    private TableColumn<StockMovement, String> moveProductCol;

    @FXML
    private TableColumn<StockMovement, String> moveSourceCol;

    @FXML
    private TableColumn<StockMovement, String> moveDestCol;

    @FXML
    private TableColumn<StockMovement, String> moveTypeCol;

    @FXML
    private TableColumn<StockMovement, Number> moveQtyCol;

    @FXML
    private TableColumn<StockMovement, String> moveRefCol;

    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private final StockMovementDAO stockMovementDAO = new StockMovementDAO();

    private final ObservableList<InventoryDisplayRow> inventoryRows = FXCollections.observableArrayList();
    private final ObservableList<StockMovement> movementRows = FXCollections.observableArrayList();

    private final Map<Integer, Product> productCache = new HashMap<>();
    private final Map<Integer, Warehouse> warehouseCache = new HashMap<>();

    @FXML
    public void initialize() {
        setupInventoryTable();
        setupMovementTable();
        loadDropdownData();
        loadInventoryData();
        loadMovementsData();
        setupMovementTypeListener();
    }

    private void setupInventoryTable() {
        if (invSkuCol != null) {
            invSkuCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSku()));
        }
        if (invProductNameCol != null) {
            invProductNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductName()));
        }
        if (invWarehouseCol != null) {
            invWarehouseCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getWarehouseName()));
        }
        if (invQtyCol != null) {
            invQtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()));
        }
        if (invStatusCol != null) {
            invStatusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        }
        if (inventoryTable != null) {
            inventoryTable.setItems(inventoryRows);
        }
    }

    private void setupMovementTable() {
        if (moveDateCol != null) {
            moveDateCol.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().getMovementDate() != null ? d.getValue().getMovementDate().format(DATE_FMT) : ""));
        }
        if (moveProductCol != null) {
            moveProductCol.setCellValueFactory(d -> {
                Product p = productCache.get(d.getValue().getProductId());
                return new SimpleStringProperty(p != null ? p.getSku() + " - " + p.getName() : "ID:" + d.getValue().getProductId());
            });
        }
        if (moveSourceCol != null) {
            moveSourceCol.setCellValueFactory(d -> {
                Warehouse w = warehouseCache.get(d.getValue().getWarehouseId());
                return new SimpleStringProperty(w != null ? w.getName() : "ID:" + d.getValue().getWarehouseId());
            });
        }
        if (moveDestCol != null) {
            moveDestCol.setCellValueFactory(d -> {
                Integer toWhId = d.getValue().getToWarehouseId();
                if (toWhId == null) return new SimpleStringProperty("-");
                Warehouse w = warehouseCache.get(toWhId);
                return new SimpleStringProperty(w != null ? w.getName() : "ID:" + toWhId);
            });
        }
        if (moveTypeCol != null) {
            moveTypeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMovementType()));
        }
        if (moveQtyCol != null) {
            moveQtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()));
        }
        if (moveRefCol != null) {
            moveRefCol.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().getReferenceNote() != null ? d.getValue().getReferenceNote() : ""));
        }
        if (movementTable != null) {
            movementTable.setItems(movementRows);
        }
    }

    private void loadDropdownData() {
        try {
            productCache.clear();
            List<Product> products = productDAO.findAllActive();
            for (Product p : products) productCache.put(p.getId(), p);

            warehouseCache.clear();
            List<Warehouse> warehouses = warehouseDAO.findAllActive();
            for (Warehouse w : warehouses) warehouseCache.put(w.getId(), w);

            if (productComboBox != null) {
                productComboBox.setItems(FXCollections.observableArrayList(products));
                productComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Product p) {
                        return p != null ? p.getSku() + " - " + p.getName() : "";
                    }
                    @Override
                    public Product fromString(String s) { return null; }
                });
            }

            StringConverter<Warehouse> whConverter = new StringConverter<>() {
                @Override
                public String toString(Warehouse w) {
                    return w != null ? w.getName() : "";
                }
                @Override
                public Warehouse fromString(String s) { return null; }
            };

            if (sourceWarehouseComboBox != null) {
                sourceWarehouseComboBox.setItems(FXCollections.observableArrayList(warehouses));
                sourceWarehouseComboBox.setConverter(whConverter);
            }
            if (destWarehouseComboBox != null) {
                destWarehouseComboBox.setItems(FXCollections.observableArrayList(warehouses));
                destWarehouseComboBox.setConverter(whConverter);
                destWarehouseComboBox.setDisable(true);
            }
            if (movementTypeComboBox != null) {
                movementTypeComboBox.setItems(FXCollections.observableArrayList("IN", "OUT", "TRANSFER", "AUDIT"));
                movementTypeComboBox.setValue("IN");
            }
        } catch (Exception e) {
            logger.error("Error loading dropdown data in InventoryController", e);
        }
    }

    private void setupMovementTypeListener() {
        if (movementTypeComboBox != null && destWarehouseComboBox != null) {
            movementTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                boolean isTransfer = "TRANSFER".equalsIgnoreCase(newVal);
                destWarehouseComboBox.setDisable(!isTransfer);
            });
        }
    }

    public void loadInventoryData() {
        try {
            inventoryRows.clear();
            List<Inventory> inventories = inventoryDAO.findAll();
            for (Inventory inv : inventories) {
                Product p = productCache.get(inv.getProductId());
                if (p == null) {
                    p = productDAO.findById(inv.getProductId()).orElse(null);
                    if (p != null) productCache.put(p.getId(), p);
                }

                Warehouse w = warehouseCache.get(inv.getWarehouseId());
                if (w == null) {
                    w = warehouseDAO.findById(inv.getWarehouseId()).orElse(null);
                    if (w != null) warehouseCache.put(w.getId(), w);
                }

                String sku = p != null ? p.getSku() : "N/A";
                String pName = p != null ? p.getName() : "Unknown Product";
                String wName = w != null ? w.getName() : "Unknown Warehouse";
                int reorder = p != null ? p.getReorderLevel() : 0;
                String status = inv.getQuantity() <= reorder ? "LOW STOCK ⚠" : "OK";

                inventoryRows.add(new InventoryDisplayRow(sku, pName, wName, inv.getQuantity(), status));
            }
        } catch (Exception e) {
            logger.error("Failed to load inventory data", e);
        }
    }

    public void loadMovementsData() {
        try {
            movementRows.clear();
            movementRows.addAll(stockMovementDAO.findAll());
        } catch (Exception e) {
            logger.error("Failed to load movements data", e);
        }
    }

    /**
     * Executes the stock movement transactionally and updates stock_movements log.
     */
    @FXML
    public void handleRecordMovement(ActionEvent event) {
        Product product = productComboBox != null ? productComboBox.getValue() : null;
        Warehouse sourceWh = sourceWarehouseComboBox != null ? sourceWarehouseComboBox.getValue() : null;
        String type = movementTypeComboBox != null ? movementTypeComboBox.getValue() : null;
        String qtyStr = quantityField != null ? quantityField.getText().trim() : "";
        String note = referenceNoteField != null ? referenceNoteField.getText().trim() : "";

        if (product == null || sourceWh == null || type == null) {
            AlertUtil.showWarning("Validation", "Please select product, warehouse, and movement type.");
            return;
        }

        if (!Validator.isPositiveInt(qtyStr)) {
            AlertUtil.showWarning("Validation", "Quantity must be a positive whole number (>= 1).");
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        Warehouse destWh = null;
        if ("TRANSFER".equalsIgnoreCase(type)) {
            destWh = destWarehouseComboBox != null ? destWarehouseComboBox.getValue() : null;
            if (destWh == null) {
                AlertUtil.showWarning("Validation", "Please select a destination warehouse for TRANSFER.");
                return;
            }
            if (destWh.getId() == sourceWh.getId()) {
                AlertUtil.showWarning("Validation", "Source and destination warehouses cannot be the same.");
                return;
            }
        }

        User user = UserSession.getInstance().getCurrentUser();
        int userId = user != null ? user.getId() : 1;

        // Perform transactional update
        try (Connection conn = DatabaseManager.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                Optional<Inventory> existingSourceInv = inventoryDAO.findByProductAndWarehouse(conn, product.getId(), sourceWh.getId());
                int currentQty = existingSourceInv.map(Inventory::getQuantity).orElse(0);

                if ("OUT".equalsIgnoreCase(type) || "TRANSFER".equalsIgnoreCase(type)) {
                    if (currentQty < quantity) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        AlertUtil.showError("Insufficient Stock",
                                String.format("Cannot execute %s: available stock is %d, requested %d. Stock cannot go negative.",
                                        type, currentQty, quantity));
                        return;
                    }
                }

                // Apply inventory changes
                switch (type.toUpperCase()) {
                    case "IN" -> {
                        inventoryDAO.upsertAddQuantity(conn, product.getId(), sourceWh.getId(), quantity);
                    }
                    case "OUT" -> {
                        int newQty = currentQty - quantity;
                        inventoryDAO.updateQuantity(conn, product.getId(), sourceWh.getId(), newQty);
                    }
                    case "TRANSFER" -> {
                        int newSourceQty = currentQty - quantity;
                        inventoryDAO.updateQuantity(conn, product.getId(), sourceWh.getId(), newSourceQty);
                        inventoryDAO.upsertAddQuantity(conn, product.getId(), destWh.getId(), quantity);
                    }
                    case "AUDIT" -> {
                        // In AUDIT mode, quantity field represents the physical counted quantity
                        if (existingSourceInv.isPresent()) {
                            inventoryDAO.updateQuantity(conn, product.getId(), sourceWh.getId(), quantity);
                        } else {
                            inventoryDAO.insert(conn, new Inventory(product.getId(), sourceWh.getId(), quantity));
                        }
                    }
                }

                // Log entry into stock_movements
                StockMovement sm = new StockMovement();
                sm.setProductId(product.getId());
                sm.setWarehouseId(sourceWh.getId());
                sm.setToWarehouseId(destWh != null ? destWh.getId() : null);
                sm.setMovementType(type.toUpperCase());
                sm.setQuantity(quantity);
                sm.setReferenceNote(note.isEmpty() ? (type + " movement") : note);
                sm.setPerformedBy(userId);

                stockMovementDAO.insert(conn, sm);

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);

                AlertUtil.showInfo("Success", "Stock movement recorded successfully.");
                if (quantityField != null) quantityField.clear();
                if (referenceNoteField != null) referenceNoteField.clear();

                loadInventoryData();
                loadMovementsData();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (Exception e) {
            logger.error("Failed to record stock movement", e);
            AlertUtil.showError("Transaction Error", "Failed to update stock: " + e.getMessage());
        }
    }

    /**
     * Display helper row for JavaFX TableView.
     */
    public static class InventoryDisplayRow {
        private final String sku;
        private final String productName;
        private final String warehouseName;
        private final int quantity;
        private final String status;

        public InventoryDisplayRow(String sku, String productName, String warehouseName, int quantity, String status) {
            this.sku = sku;
            this.productName = productName;
            this.warehouseName = warehouseName;
            this.quantity = quantity;
            this.status = status;
        }

        public String getSku() { return sku; }
        public String getProductName() { return productName; }
        public String getWarehouseName() { return warehouseName; }
        public int getQuantity() { return quantity; }
        public String getStatus() { return status; }
    }
}
