package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.StockMovement;

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
 * Data Access Object for 'stock_movements' table using standard JDBC PreparedStatement.
 */
public class StockMovementDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds stock movement by ID.
     *
     * @param id movement ID
     * @return Optional StockMovement
     */
    public Optional<StockMovement> findById(int id) {
        String sql = "SELECT id, product_id, warehouse_id, to_warehouse_id, movement_type, quantity, " +
                     "reference_note, performed_by, movement_date FROM stock_movements WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStockMovement(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding stock movement by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all stock movements ordered by date descending.
     *
     * @return list of movements
     */
    public List<StockMovement> findAll() {
        String sql = "SELECT id, product_id, warehouse_id, to_warehouse_id, movement_type, quantity, " +
                     "reference_note, performed_by, movement_date FROM stock_movements ORDER BY movement_date DESC, id DESC";
        List<StockMovement> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStockMovement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all stock movements", e);
        }
        return list;
    }

    /**
     * Retrieves all stock movements for a specific product.
     *
     * @param productId product ID
     * @return list of movements
     */
    public List<StockMovement> findByProductId(int productId) {
        String sql = "SELECT id, product_id, warehouse_id, to_warehouse_id, movement_type, quantity, " +
                     "reference_note, performed_by, movement_date FROM stock_movements WHERE product_id = ? " +
                     "ORDER BY movement_date DESC";
        List<StockMovement> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStockMovement(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding stock movements for product: " + productId, e);
        }
        return list;
    }

    /**
     * Retrieves all stock movements for a specific warehouse.
     *
     * @param warehouseId warehouse ID
     * @return list of movements
     */
    public List<StockMovement> findByWarehouseId(int warehouseId) {
        String sql = "SELECT id, product_id, warehouse_id, to_warehouse_id, movement_type, quantity, " +
                     "reference_note, performed_by, movement_date FROM stock_movements " +
                     "WHERE warehouse_id = ? OR to_warehouse_id = ? ORDER BY movement_date DESC";
        List<StockMovement> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warehouseId);
            ps.setInt(2, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStockMovement(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding stock movements for warehouse: " + warehouseId, e);
        }
        return list;
    }

    /**
     * Inserts a stock movement audit record.
     *
     * @param sm movement to insert
     * @return generated ID
     */
    public int insert(StockMovement sm) {
        try (Connection conn = getConnection()) {
            return insert(conn, sm);
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting stock movement", e);
        }
    }

    /**
     * Inserts a stock movement audit record within a transaction connection.
     *
     * @param conn transaction connection
     * @param sm   movement to insert
     * @return generated ID
     */
    public int insert(Connection conn, StockMovement sm) throws SQLException {
        String sql = "INSERT INTO stock_movements (product_id, warehouse_id, to_warehouse_id, movement_type, " +
                     "quantity, reference_note, performed_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, sm.getProductId());
            ps.setInt(2, sm.getWarehouseId());
            if (sm.getToWarehouseId() != null) {
                ps.setInt(3, sm.getToWarehouseId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, sm.getMovementType());
            ps.setInt(5, sm.getQuantity());
            ps.setString(6, sm.getReferenceNote());
            ps.setInt(7, sm.getPerformedBy());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    sm.setId(id);
                    return id;
                }
            }
        }
        return sm.getId();
    }

    /**
     * Deletes a stock movement record by ID (if needed for rollback/testing).
     *
     * @param id movement ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM stock_movements WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting stock movement: " + id, e);
        }
    }

    private StockMovement mapResultSetToStockMovement(ResultSet rs) throws SQLException {
        StockMovement sm = new StockMovement();
        sm.setId(rs.getInt("id"));
        sm.setProductId(rs.getInt("product_id"));
        sm.setWarehouseId(rs.getInt("warehouse_id"));

        int toWh = rs.getInt("to_warehouse_id");
        if (!rs.wasNull()) {
            sm.setToWarehouseId(toWh);
        }

        sm.setMovementType(rs.getString("movement_type"));
        sm.setQuantity(rs.getInt("quantity"));
        sm.setReferenceNote(rs.getString("reference_note"));
        sm.setPerformedBy(rs.getInt("performed_by"));

        Timestamp ts = rs.getTimestamp("movement_date");
        if (ts != null) {
            sm.setMovementDate(ts.toLocalDateTime());
        }
        return sm;
    }
}
