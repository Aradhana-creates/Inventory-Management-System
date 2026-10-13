package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for 'products' table using standard JDBC PreparedStatement.
 */
public class ProductDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds a product by its ID.
     *
     * @param id product ID
     * @return Optional Product
     */
    public Optional<Product> findById(int id) {
        String sql = "SELECT id, sku, name, description, category_id, unit_price, reorder_level, active, created_at, updated_at " +
                     "FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Finds a product by its unique SKU.
     *
     * @param sku product SKU
     * @return Optional Product
     */
    public Optional<Product> findBySku(String sku) {
        String sql = "SELECT id, sku, name, description, category_id, unit_price, reorder_level, active, created_at, updated_at " +
                     "FROM products WHERE sku = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by sku: " + sku, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all products.
     *
     * @return list of all products
     */
    public List<Product> findAll() {
        String sql = "SELECT id, sku, name, description, category_id, unit_price, reorder_level, active, created_at, updated_at " +
                     "FROM products ORDER BY id ASC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products", e);
        }
        return list;
    }

    /**
     * Retrieves all active products.
     *
     * @return list of active products
     */
    public List<Product> findAllActive() {
        String sql = "SELECT id, sku, name, description, category_id, unit_price, reorder_level, active, created_at, updated_at " +
                     "FROM products WHERE active = 1 ORDER BY name ASC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active products", e);
        }
        return list;
    }

    /**
     * Retrieves products belonging to a specific category.
     *
     * @param categoryId category ID
     * @return list of products
     */
    public List<Product> findByCategoryId(int categoryId) {
        String sql = "SELECT id, sku, name, description, category_id, unit_price, reorder_level, active, created_at, updated_at " +
                     "FROM products WHERE category_id = ? AND active = 1 ORDER BY name ASC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category: " + categoryId, e);
        }
        return list;
    }

    /**
     * Searches active products by keyword in SKU, name, or description.
     *
     * @param keyword search keyword
     * @return matched products
     */
    public List<Product> search(String keyword) {
        String sql = "SELECT id, sku, name, description, category_id, unit_price, reorder_level, active, created_at, updated_at " +
                     "FROM products WHERE (sku LIKE ? OR name LIKE ? OR description LIKE ?) AND active = 1 " +
                     "ORDER BY name ASC";
        List<Product> list = new ArrayList<>();
        String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching products with keyword: " + keyword, e);
        }
        return list;
    }

    /**
     * Retrieves products that are at or below their reorder level across all warehouses.
     *
     * @return list of low stock products
     */
    public List<Product> findLowStockProducts() {
        String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.unit_price, p.reorder_level, p.active, " +
                     "       p.created_at, p.updated_at, COALESCE(SUM(i.quantity), 0) AS total_stock " +
                     "FROM products p " +
                     "LEFT JOIN inventory i ON p.id = i.product_id " +
                     "WHERE p.active = 1 " +
                     "GROUP BY p.id " +
                     "HAVING total_stock <= p.reorder_level " +
                     "ORDER BY total_stock ASC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding low stock products", e);
        }
        return list;
    }

    /**
     * Inserts a new product record.
     *
     * @param product product to insert
     * @return generated product ID
     */
    public int insert(Product product) {
        String sql = "INSERT INTO products (sku, name, description, category_id, unit_price, reorder_level, active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getSku());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            if (product.getCategoryId() != null) {
                ps.setInt(4, product.getCategoryId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(5, product.getUnitPrice());
            ps.setInt(6, product.getReorderLevel());
            ps.setBoolean(7, product.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    product.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting product: " + product.getSku(), e);
        }
        return product.getId();
    }

    /**
     * Updates an existing product.
     *
     * @param product product with updated details
     * @return true if updated successfully
     */
    public boolean update(Product product) {
        String sql = "UPDATE products SET sku = ?, name = ?, description = ?, category_id = ?, " +
                     "unit_price = ?, reorder_level = ?, active = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getSku());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            if (product.getCategoryId() != null) {
                ps.setInt(4, product.getCategoryId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(5, product.getUnitPrice());
            ps.setInt(6, product.getReorderLevel());
            ps.setBoolean(7, product.isActive());
            ps.setInt(8, product.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product: " + product.getId(), e);
        }
    }

    /**
     * Soft-deletes (deactivates) a product.
     *
     * @param id product ID
     * @return true if deactivated
     */
    public boolean deactivate(int id) {
        String sql = "UPDATE products SET active = 0 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating product: " + id, e);
        }
    }

    /**
     * Permanently deletes a product by ID.
     *
     * @param id product ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product: " + id, e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setSku(rs.getString("sku"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));

        int catId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            p.setCategoryId(catId);
        }

        p.setUnitPrice(rs.getBigDecimal("unit_price"));
        p.setReorderLevel(rs.getInt("reorder_level"));
        p.setActive(rs.getBoolean("active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            p.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            p.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return p;
    }
}
