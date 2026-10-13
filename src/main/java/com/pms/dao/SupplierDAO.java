package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.Supplier;

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
 * Data Access Object for 'suppliers' table using standard JDBC PreparedStatement.
 */
public class SupplierDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds a supplier by its ID.
     *
     * @param id supplier ID
     * @return Optional Supplier
     */
    public Optional<Supplier> findById(int id) {
        String sql = "SELECT id, name, contact_person, phone, email, address, active, created_at, updated_at " +
                     "FROM suppliers WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Finds a supplier by name.
     *
     * @param name supplier name
     * @return Optional Supplier
     */
    public Optional<Supplier> findByName(String name) {
        String sql = "SELECT id, name, contact_person, phone, email, address, active, created_at, updated_at " +
                     "FROM suppliers WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier by name: " + name, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all suppliers.
     *
     * @return list of all suppliers
     */
    public List<Supplier> findAll() {
        String sql = "SELECT id, name, contact_person, phone, email, address, active, created_at, updated_at " +
                     "FROM suppliers ORDER BY id ASC";
        List<Supplier> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all suppliers", e);
        }
        return list;
    }

    /**
     * Retrieves all active suppliers.
     *
     * @return list of active suppliers
     */
    public List<Supplier> findAllActive() {
        String sql = "SELECT id, name, contact_person, phone, email, address, active, created_at, updated_at " +
                     "FROM suppliers WHERE active = 1 ORDER BY name ASC";
        List<Supplier> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active suppliers", e);
        }
        return list;
    }

    /**
     * Inserts a new supplier.
     *
     * @param supplier supplier to insert
     * @return generated supplier ID
     */
    public int insert(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address, active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setBoolean(6, supplier.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    supplier.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting supplier: " + supplier.getName(), e);
        }
        return supplier.getId();
    }

    /**
     * Updates an existing supplier.
     *
     * @param supplier supplier with updated details
     * @return true if updated successfully
     */
    public boolean update(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ?, active = ? " +
                     "WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setBoolean(6, supplier.isActive());
            ps.setInt(7, supplier.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating supplier: " + supplier.getId(), e);
        }
    }

    /**
     * Deactivates a supplier (soft delete).
     *
     * @param id supplier ID
     * @return true if deactivated
     */
    public boolean deactivate(int id) {
        String sql = "UPDATE suppliers SET active = 0 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating supplier: " + id, e);
        }
    }

    /**
     * Permanently deletes a supplier by ID.
     *
     * @param id supplier ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting supplier: " + id, e);
        }
    }

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setContactPerson(rs.getString("contact_person"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        s.setActive(rs.getBoolean("active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            s.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            s.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return s;
    }
}
