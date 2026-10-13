package com.pms.ui;

import com.ims.common.util.AlertUtil;
import com.ims.common.util.Validator;
import com.pms.dao.CategoryDAO;
import com.pms.dao.ProductDAO;
import com.pms.model.Category;
import com.pms.model.Product;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for Product management view. Provides TableView operations for adding,
 * editing, searching, and deleting products mapped to ProductDAO.
 */
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Number> idCol;

    @FXML
    private TableColumn<Product, String> skuCol;

    @FXML
    private TableColumn<Product, String> nameCol;

    @FXML
    private TableColumn<Product, String> categoryCol;

    @FXML
    private TableColumn<Product, BigDecimal> priceCol;

    @FXML
    private TableColumn<Product, Number> reorderCol;

    @FXML
    private TableColumn<Product, Boolean> activeCol;

    @FXML
    private TextField searchField;

    @FXML
    private TextField skuField;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private TextField unitPriceField;

    @FXML
    private TextField reorderLevelField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final Map<Integer, String> categoryMap = new HashMap<>();

    private Product selectedProduct;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadCategories();
        loadProducts();
        setupTableSelection();
        setupRoleAccess();
    }

    private void setupTableColumns() {
        if (idCol != null) {
            idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        }
        if (skuCol != null) {
            skuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSku()));
        }
        if (nameCol != null) {
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        }
        if (categoryCol != null) {
            categoryCol.setCellValueFactory(data -> {
                Integer catId = data.getValue().getCategoryId();
                String catName = (catId != null && categoryMap.containsKey(catId)) ? categoryMap.get(catId) : "-";
                return new SimpleStringProperty(catName);
            });
        }
        if (priceCol != null) {
            priceCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getUnitPrice()));
        }
        if (reorderCol != null) {
            reorderCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getReorderLevel()));
        }
        if (activeCol != null) {
            activeCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isActive()));
        }
        if (productTable != null) {
            productTable.setItems(productList);
        }
    }

    private void loadCategories() {
        try {
            categoryMap.clear();
            List<Category> categories = categoryDAO.findAll();
            for (Category c : categories) {
                categoryMap.put(c.getId(), c.getName());
            }

            if (categoryComboBox != null) {
                categoryComboBox.setItems(FXCollections.observableArrayList(categories));
                categoryComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Category object) {
                        return object != null ? object.getName() : "";
                    }

                    @Override
                    public Category fromString(String string) {
                        return null;
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Error loading categories", e);
        }
    }

    public void loadProducts() {
        try {
            productList.clear();
            productList.addAll(productDAO.findAll());
        } catch (Exception e) {
            logger.error("Error loading products", e);
            AlertUtil.showError("Database Error", "Failed to load products from database.");
        }
    }

    private void setupTableSelection() {
        if (productTable != null) {
            productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    populateForm(newVal);
                }
            });
        }
    }

    private void populateForm(Product product) {
        this.selectedProduct = product;
        if (skuField != null) skuField.setText(product.getSku());
        if (nameField != null) nameField.setText(product.getName());
        if (descriptionField != null) descriptionField.setText(product.getDescription() != null ? product.getDescription() : "");
        if (unitPriceField != null) unitPriceField.setText(product.getUnitPrice() != null ? product.getUnitPrice().toString() : "");
        if (reorderLevelField != null) reorderLevelField.setText(String.valueOf(product.getReorderLevel()));

        if (categoryComboBox != null && product.getCategoryId() != null) {
            for (Category c : categoryComboBox.getItems()) {
                if (c.getId() == product.getCategoryId()) {
                    categoryComboBox.setValue(c);
                    break;
                }
            }
        }
    }

    private void setupRoleAccess() {
        boolean canManage = UserSession.getInstance().hasAnyRole("ADMIN", "MANAGER");
        if (addButton != null) addButton.setDisable(!canManage);
        if (updateButton != null) updateButton.setDisable(!canManage);
        if (deleteButton != null) deleteButton.setDisable(!canManage);
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        String keyword = searchField != null ? searchField.getText().trim() : "";
        try {
            productList.clear();
            if (keyword.isEmpty()) {
                productList.addAll(productDAO.findAll());
            } else {
                productList.addAll(productDAO.search(keyword));
            }
        } catch (Exception e) {
            logger.error("Search failed for keyword: {}", keyword, e);
        }
    }

    @FXML
    public void handleAddProduct(ActionEvent event) {
        if (!validateProductInputs()) return;

        try {
            String sku = skuField.getText().trim();
            if (productDAO.findBySku(sku).isPresent()) {
                AlertUtil.showWarning("Duplicate SKU", "A product with SKU '" + sku + "' already exists.");
                return;
            }

            Product p = new Product();
            p.setSku(sku);
            p.setName(nameField.getText().trim());
            p.setDescription(descriptionField != null ? descriptionField.getText().trim() : "");
            p.setUnitPrice(new BigDecimal(unitPriceField.getText().trim()));
            p.setReorderLevel(Integer.parseInt(reorderLevelField.getText().trim()));
            p.setActive(true);

            Category cat = categoryComboBox != null ? categoryComboBox.getValue() : null;
            if (cat != null) {
                p.setCategoryId(cat.getId());
            }

            productDAO.insert(p);
            AlertUtil.showInfo("Product Added", "Product '" + p.getName() + "' was added successfully.");
            loadProducts();
            handleClearForm(null);
        } catch (Exception e) {
            logger.error("Failed to add product", e);
            AlertUtil.showError("Save Error", "Could not add product: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdateProduct(ActionEvent event) {
        if (selectedProduct == null) {
            AlertUtil.showWarning("No Selection", "Please select a product from the table to update.");
            return;
        }

        if (!validateProductInputs()) return;

        try {
            String newSku = skuField.getText().trim();
            Optional<Product> existingWithSku = productDAO.findBySku(newSku);
            if (existingWithSku.isPresent() && existingWithSku.get().getId() != selectedProduct.getId()) {
                AlertUtil.showWarning("Duplicate SKU", "Another product with SKU '" + newSku + "' already exists.");
                return;
            }

            selectedProduct.setSku(newSku);
            selectedProduct.setName(nameField.getText().trim());
            selectedProduct.setDescription(descriptionField != null ? descriptionField.getText().trim() : "");
            selectedProduct.setUnitPrice(new BigDecimal(unitPriceField.getText().trim()));
            selectedProduct.setReorderLevel(Integer.parseInt(reorderLevelField.getText().trim()));

            Category cat = categoryComboBox != null ? categoryComboBox.getValue() : null;
            if (cat != null) {
                selectedProduct.setCategoryId(cat.getId());
            }

            productDAO.update(selectedProduct);
            AlertUtil.showInfo("Product Updated", "Product '" + selectedProduct.getName() + "' updated successfully.");
            loadProducts();
            handleClearForm(null);
        } catch (Exception e) {
            logger.error("Failed to update product", e);
            AlertUtil.showError("Update Error", "Could not update product: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteProduct(ActionEvent event) {
        if (selectedProduct == null) {
            AlertUtil.showWarning("No Selection", "Please select a product from the table to deactivate/delete.");
            return;
        }

        Optional<ButtonType> confirm = AlertUtil.showConfirm("Confirm Delete",
                "Are you sure you want to deactivate product '" + selectedProduct.getName() + "'?");

        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            try {
                productDAO.deactivate(selectedProduct.getId());
                AlertUtil.showInfo("Product Deactivated", "Product has been deactivated.");
                loadProducts();
                handleClearForm(null);
            } catch (Exception e) {
                logger.error("Failed to deactivate product", e);
                AlertUtil.showError("Delete Error", "Could not deactivate product: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleClearForm(ActionEvent event) {
        this.selectedProduct = null;
        if (skuField != null) skuField.clear();
        if (nameField != null) nameField.clear();
        if (descriptionField != null) descriptionField.clear();
        if (unitPriceField != null) unitPriceField.clear();
        if (reorderLevelField != null) reorderLevelField.clear();
        if (categoryComboBox != null) categoryComboBox.setValue(null);
        if (productTable != null) productTable.getSelectionModel().clearSelection();
    }

    private boolean validateProductInputs() {
        if (Validator.isNullOrEmpty(skuField != null ? skuField.getText() : null)) {
            AlertUtil.showWarning("Validation", "SKU cannot be empty.");
            return false;
        }
        if (Validator.isNullOrEmpty(nameField != null ? nameField.getText() : null)) {
            AlertUtil.showWarning("Validation", "Product Name cannot be empty.");
            return false;
        }
        if (unitPriceField == null || !Validator.isNonNegativeDecimal(unitPriceField.getText())) {
            AlertUtil.showWarning("Validation", "Unit price must be a valid non-negative decimal.");
            return false;
        }
        if (reorderLevelField == null || !Validator.isNonNegativeInt(reorderLevelField.getText())) {
            AlertUtil.showWarning("Validation", "Reorder level must be a non-negative whole number.");
            return false;
        }
        return true;
    }
}
