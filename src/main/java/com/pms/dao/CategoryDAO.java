package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.Category;

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
 * Data Access Object for 'categories' table using standard JDBC PreparedStatement.
 */
public class CategoryDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds a category by its ID.
     *
     * @param id category ID
     * @return Optional Category
     */
    public Optional<Category> findById(int id) {
        String sql = "SELECT id, name, description, created_at FROM categories WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Finds a category by its unique name.
     *
     * @param name category name
     * @return Optional Category
     */
    public Optional<Category> findByName(String name) {
        String sql = "SELECT id, name, description, created_at FROM categories WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by name: " + name, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all categories ordered by name.
     *
     * @return list of categories
     */
    public List<Category> findAll() {
        String sql = "SELECT id, name, description, created_at FROM categories ORDER BY name ASC";
        List<Category> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all categories", e);
        }
        return list;
    }

    /**
     * Inserts a new category.
     *
     * @param category the Category to insert
     * @return generated category ID
     */
    public int insert(Category category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    category.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting category: " + category.getName(), e);
        }
        return category.getId();
    }

    /**
     * Updates an existing category.
     *
     * @param category the Category to update
     * @return true if updated successfully
     */
    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category: " + category.getId(), e);
        }
    }

    /**
     * Deletes a category by ID.
     *
     * @param id category ID
     * @return true if deleted successfully
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category: " + id, e);
        }
    }

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            c.setCreatedAt(ts.toLocalDateTime());
        }
        return c;
    }
}
