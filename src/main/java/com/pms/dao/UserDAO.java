package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.User;

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
 * Data Access Object for 'users' and role lookups using standard JDBC PreparedStatement.
 */
public class UserDAO {

    /**
     * Obtains a database connection from the connection pool.
     */
    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds a user by primary key ID.
     *
     * @param id the user ID
     * @return Optional containing the User if found, empty otherwise
     */
    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, password_hash, full_name, email, role, active, created_at, updated_at " +
                     "FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Finds a user by unique username.
     *
     * @param username the username to look up
     * @return Optional containing the User if found, empty otherwise
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, full_name, email, role, active, created_at, updated_at " +
                     "FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + username, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all users.
     *
     * @return list of all users
     */
    public List<User> findAll() {
        String sql = "SELECT id, username, password_hash, full_name, email, role, active, created_at, updated_at " +
                     "FROM users ORDER BY id ASC";
        List<User> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
        return list;
    }

    /**
     * Retrieves all active users.
     *
     * @return list of active users
     */
    public List<User> findAllActive() {
        String sql = "SELECT id, username, password_hash, full_name, email, role, active, created_at, updated_at " +
                     "FROM users WHERE active = 1 ORDER BY id ASC";
        List<User> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active users", e);
        }
        return list;
    }

    /**
     * Inserts a new user record.
     *
     * @param user the User to insert
     * @return generated user ID
     */
    public int insert(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole());
            ps.setBoolean(6, user.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user: " + user.getUsername(), e);
        }
        return user.getId();
    }

    /**
     * Updates an existing user's details (full_name, email, role, active).
     *
     * @param user the user with updated data
     * @return true if updated successfully
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, role = ?, active = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole());
            ps.setBoolean(4, user.isActive());
            ps.setInt(5, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + user.getId(), e);
        }
    }

    /**
     * Updates a user's password hash.
     *
     * @param userId       the ID of the user
     * @param passwordHash the new BCrypt password hash
     * @return true if updated successfully
     */
    public boolean updatePassword(int userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password for user: " + userId, e);
        }
    }

    /**
     * Deactivates a user (soft delete).
     *
     * @param id the user ID
     * @return true if deactivated successfully
     */
    public boolean deactivate(int id) {
        String sql = "UPDATE users SET active = 0 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating user: " + id, e);
        }
    }

    /**
     * Reactivates a user.
     *
     * @param id the user ID
     * @return true if reactivated successfully
     */
    public boolean reactivate(int id) {
        String sql = "UPDATE users SET active = 1 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error reactivating user: " + id, e);
        }
    }

    /**
     * Permanently deletes a user by ID.
     *
     * @param id the user ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + id, e);
        }
    }

    /**
     * Maps a ResultSet row to a User entity.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return user;
    }
}
