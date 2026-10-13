package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.Warehouse;

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
 * Data Access Object for 'warehouses' table using standard JDBC PreparedStatement.
 */
public class WarehouseDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds a warehouse by its ID.
     *
     * @param id warehouse ID
     * @return Optional Warehouse
     */
    public Optional<Warehouse> findById(int id) {
        String sql = "SELECT id, name, location, capacity, active, created_at FROM warehouses WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWarehouse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding warehouse by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Finds a warehouse by its unique name.
     *
     * @param name warehouse name
     * @return Optional Warehouse
     */
    public Optional<Warehouse> findByName(String name) {
        String sql = "SELECT id, name, location, capacity, active, created_at FROM warehouses WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWarehouse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding warehouse by name: " + name, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all warehouses.
     *
     * @return list of all warehouses
     */
    public List<Warehouse> findAll() {
        String sql = "SELECT id, name, location, capacity, active, created_at FROM warehouses ORDER BY id ASC";
        List<Warehouse> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToWarehouse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all warehouses", e);
        }
        return list;
    }

    /**
     * Retrieves all active warehouses.
     *
     * @return list of active warehouses
     */
    public List<Warehouse> findAllActive() {
        String sql = "SELECT id, name, location, capacity, active, created_at FROM warehouses WHERE active = 1 ORDER BY name ASC";
        List<Warehouse> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToWarehouse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active warehouses", e);
        }
        return list;
    }

    /**
     * Inserts a new warehouse.
     *
     * @param warehouse warehouse to insert
     * @return generated warehouse ID
     */
    public int insert(Warehouse warehouse) {
        String sql = "INSERT INTO warehouses (name, location, capacity, active) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, warehouse.getName());
            ps.setString(2, warehouse.getLocation());
            ps.setInt(3, warehouse.getCapacity());
            ps.setBoolean(4, warehouse.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    warehouse.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting warehouse: " + warehouse.getName(), e);
        }
        return warehouse.getId();
    }

    /**
     * Updates an existing warehouse.
     *
     * @param warehouse warehouse with updated details
     * @return true if updated successfully
     */
    public boolean update(Warehouse warehouse) {
        String sql = "UPDATE warehouses SET name = ?, location = ?, capacity = ?, active = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouse.getName());
            ps.setString(2, warehouse.getLocation());
            ps.setInt(3, warehouse.getCapacity());
            ps.setBoolean(4, warehouse.isActive());
            ps.setInt(5, warehouse.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating warehouse: " + warehouse.getId(), e);
        }
    }

    /**
     * Deactivates a warehouse (soft delete).
     *
     * @param id warehouse ID
     * @return true if deactivated
     */
    public boolean deactivate(int id) {
        String sql = "UPDATE warehouses SET active = 0 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating warehouse: " + id, e);
        }
    }

    /**
     * Permanently deletes a warehouse by ID.
     *
     * @param id warehouse ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM warehouses WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting warehouse: " + id, e);
        }
    }

    private Warehouse mapResultSetToWarehouse(ResultSet rs) throws SQLException {
        Warehouse w = new Warehouse();
        w.setId(rs.getInt("id"));
        w.setName(rs.getString("name"));
        w.setLocation(rs.getString("location"));
        w.setCapacity(rs.getInt("capacity"));
        w.setActive(rs.getBoolean("active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            w.setCreatedAt(createdAt.toLocalDateTime());
        }
        return w;
    }
}
