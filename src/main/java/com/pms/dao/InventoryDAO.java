package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.Inventory;

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
 * Data Access Object for 'inventory' table using standard JDBC PreparedStatement.
 * Supports overloaded transaction methods accepting an external {@link Connection}.
 */
public class InventoryDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds inventory entry by ID.
     *
     * @param id inventory ID
     * @return Optional Inventory
     */
    public Optional<Inventory> findById(int id) {
        String sql = "SELECT id, product_id, warehouse_id, quantity, updated_at FROM inventory WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Finds inventory entry for a specific product and warehouse combination.
     *
     * @param productId   product ID
     * @param warehouseId warehouse ID
     * @return Optional Inventory
     */
    public Optional<Inventory> findByProductAndWarehouse(int productId, int warehouseId) {
        String sql = "SELECT id, product_id, warehouse_id, quantity, updated_at " +
                     "FROM inventory WHERE product_id = ? AND warehouse_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory for product " + productId + " at warehouse " + warehouseId, e);
        }
        return Optional.empty();
    }

    /**
     * Finds inventory entry for a specific product and warehouse combination using an existing connection.
     *
     * @param conn        transaction connection
     * @param productId   product ID
     * @param warehouseId warehouse ID
     * @return Optional Inventory
     */
    public Optional<Inventory> findByProductAndWarehouse(Connection conn, int productId, int warehouseId) throws SQLException {
        String sql = "SELECT id, product_id, warehouse_id, quantity, updated_at " +
                     "FROM inventory WHERE product_id = ? AND warehouse_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInventory(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds all inventory records for a given product across warehouses.
     *
     * @param productId product ID
     * @return list of inventory entries
     */
    public List<Inventory> findByProductId(int productId) {
        String sql = "SELECT id, product_id, warehouse_id, quantity, updated_at FROM inventory WHERE product_id = ?";
        List<Inventory> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory by product id: " + productId, e);
        }
        return list;
    }

    /**
     * Finds all inventory records for a given warehouse.
     *
     * @param warehouseId warehouse ID
     * @return list of inventory entries
     */
    public List<Inventory> findByWarehouseId(int warehouseId) {
        String sql = "SELECT id, product_id, warehouse_id, quantity, updated_at FROM inventory WHERE warehouse_id = ?";
        List<Inventory> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory by warehouse id: " + warehouseId, e);
        }
        return list;
    }

    /**
     * Retrieves all inventory records.
     *
     * @return list of all inventory entries
     */
    public List<Inventory> findAll() {
        String sql = "SELECT id, product_id, warehouse_id, quantity, updated_at FROM inventory ORDER BY id ASC";
        List<Inventory> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToInventory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all inventory", e);
        }
        return list;
    }

    /**
     * Gets the sum of stock for a product across all warehouses.
     *
     * @param productId product ID
     * @return total stock
     */
    public int getTotalQuantityByProduct(int productId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) AS total_qty FROM inventory WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_qty");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting total quantity for product: " + productId, e);
        }
        return 0;
    }

    /**
     * Inserts an inventory record.
     *
     * @param inventory inventory to insert
     * @return generated ID
     */
    public int insert(Inventory inventory) {
        String sql = "INSERT INTO inventory (product_id, warehouse_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, inventory.getProductId());
            ps.setInt(2, inventory.getWarehouseId());
            ps.setInt(3, inventory.getQuantity());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    inventory.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting inventory", e);
        }
        return inventory.getId();
    }

    /**
     * Inserts an inventory record using an existing connection.
     *
     * @param conn      transaction connection
     * @param inventory inventory to insert
     * @return generated ID
     */
    public int insert(Connection conn, Inventory inventory) throws SQLException {
        String sql = "INSERT INTO inventory (product_id, warehouse_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, inventory.getProductId());
            ps.setInt(2, inventory.getWarehouseId());
            ps.setInt(3, inventory.getQuantity());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    inventory.setId(id);
                    return id;
                }
            }
        }
        return inventory.getId();
    }

    /**
     * Updates quantity for product and warehouse.
     *
     * @param productId   product ID
     * @param warehouseId warehouse ID
     * @param newQuantity new quantity (must be >= 0)
     * @return true if updated
     */
    public boolean updateQuantity(int productId, int warehouseId, int newQuantity) {
        String sql = "UPDATE inventory SET quantity = ? WHERE product_id = ? AND warehouse_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, productId);
            ps.setInt(3, warehouseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating inventory quantity", e);
        }
    }

    /**
     * Updates quantity for product and warehouse using a transaction connection.
     *
     * @param conn        transaction connection
     * @param productId   product ID
     * @param warehouseId warehouse ID
     * @param newQuantity new quantity
     * @return true if updated
     */
    public boolean updateQuantity(Connection conn, int productId, int warehouseId, int newQuantity) throws SQLException {
        String sql = "UPDATE inventory SET quantity = ? WHERE product_id = ? AND warehouse_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, productId);
            ps.setInt(3, warehouseId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Upserts (insert or update on duplicate key) inventory quantity in a transaction.
     *
     * @param conn        transaction connection
     * @param productId   product ID
     * @param warehouseId warehouse ID
     * @param delta       quantity to add (or subtract if negative)
     * @return true if success
     */
    public boolean upsertAddQuantity(Connection conn, int productId, int warehouseId, int delta) throws SQLException {
        String sql = "INSERT INTO inventory (product_id, warehouse_id, quantity) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, warehouseId);
            ps.setInt(3, delta);
            ps.setInt(4, delta);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes inventory record by ID.
     *
     * @param id inventory ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM inventory WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting inventory: " + id, e);
        }
    }

    private Inventory mapResultSetToInventory(ResultSet rs) throws SQLException {
        Inventory inv = new Inventory();
        inv.setId(rs.getInt("id"));
        inv.setProductId(rs.getInt("product_id"));
        inv.setWarehouseId(rs.getInt("warehouse_id"));
        inv.setQuantity(rs.getInt("quantity"));
        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) {
            inv.setUpdatedAt(ts.toLocalDateTime());
        }
        return inv;
    }
}
