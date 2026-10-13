package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.PurchaseOrder;
import com.pms.model.PurchaseOrderItem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for 'purchase_orders' and 'purchase_order_items' using standard JDBC PreparedStatement.
 */
public class PurchaseOrderDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds a purchase order by ID, including its line items.
     *
     * @param id purchase order ID
     * @return Optional PurchaseOrder
     */
    public Optional<PurchaseOrder> findById(int id) {
        String sql = "SELECT id, supplier_id, warehouse_id, status, order_date, expected_date, received_date, " +
                     "notes, created_by, approved_by, created_at, updated_at FROM purchase_orders WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PurchaseOrder po = mapResultSetToPurchaseOrder(rs);
                    po.setItems(getItemsByPoId(conn, po.getId()));
                    return Optional.of(po);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase order by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all purchase orders.
     *
     * @return list of purchase orders
     */
    public List<PurchaseOrder> findAll() {
        String sql = "SELECT id, supplier_id, warehouse_id, status, order_date, expected_date, received_date, " +
                     "notes, created_by, approved_by, created_at, updated_at FROM purchase_orders ORDER BY id DESC";
        List<PurchaseOrder> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PurchaseOrder po = mapResultSetToPurchaseOrder(rs);
                po.setItems(getItemsByPoId(conn, po.getId()));
                list.add(po);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all purchase orders", e);
        }
        return list;
    }

    /**
     * Retrieves purchase orders filtered by status.
     *
     * @param status status (e.g. 'PENDING', 'APPROVED', 'RECEIVED', 'REJECTED')
     * @return list of purchase orders
     */
    public List<PurchaseOrder> findByStatus(String status) {
        String sql = "SELECT id, supplier_id, warehouse_id, status, order_date, expected_date, received_date, " +
                     "notes, created_by, approved_by, created_at, updated_at FROM purchase_orders WHERE status = ? ORDER BY id DESC";
        List<PurchaseOrder> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder po = mapResultSetToPurchaseOrder(rs);
                    po.setItems(getItemsByPoId(conn, po.getId()));
                    list.add(po);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase orders by status: " + status, e);
        }
        return list;
    }

    /**
     * Inserts a purchase order along with its line items within a transaction.
     *
     * @param po purchase order to insert
     * @return generated purchase order ID
     */
    public int insert(PurchaseOrder po) {
        String poSql = "INSERT INTO purchase_orders (supplier_id, warehouse_id, status, order_date, expected_date, notes, created_by) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(poSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, po.getSupplierId());
                ps.setInt(2, po.getWarehouseId());
                ps.setString(3, po.getStatus() != null ? po.getStatus() : "PENDING");
                ps.setDate(4, po.getOrderDate() != null ? Date.valueOf(po.getOrderDate()) : Date.valueOf(LocalDate.now()));
                if (po.getExpectedDate() != null) {
                    ps.setDate(5, Date.valueOf(po.getExpectedDate()));
                } else {
                    ps.setNull(5, java.sql.Types.DATE);
                }
                ps.setString(6, po.getNotes());
                ps.setInt(7, po.getCreatedBy());

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int poId = rs.getInt(1);
                        po.setId(poId);
                    }
                }

                if (po.getItems() != null && !po.getItems().isEmpty()) {
                    for (PurchaseOrderItem item : po.getItems()) {
                        item.setPoId(po.getId());
                        insertItem(conn, item);
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return po.getId();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting purchase order", e);
        }
    }

    /**
     * Updates an existing purchase order header.
     *
     * @param po purchase order to update
     * @return true if updated
     */
    public boolean update(PurchaseOrder po) {
        String sql = "UPDATE purchase_orders SET supplier_id = ?, warehouse_id = ?, status = ?, " +
                     "order_date = ?, expected_date = ?, received_date = ?, notes = ?, approved_by = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, po.getSupplierId());
            ps.setInt(2, po.getWarehouseId());
            ps.setString(3, po.getStatus());
            ps.setDate(4, po.getOrderDate() != null ? Date.valueOf(po.getOrderDate()) : null);
            if (po.getExpectedDate() != null) {
                ps.setDate(5, Date.valueOf(po.getExpectedDate()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            if (po.getReceivedDate() != null) {
                ps.setDate(6, Date.valueOf(po.getReceivedDate()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setString(7, po.getNotes());
            if (po.getApprovedBy() != null) {
                ps.setInt(8, po.getApprovedBy());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            ps.setInt(9, po.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating purchase order: " + po.getId(), e);
        }
    }

    /**
     * Updates the status and approved_by fields of a purchase order.
     *
     * @param poId       purchase order ID
     * @param status     new status
     * @param approvedBy user ID approving
     * @return true if updated
     */
    public boolean updateStatus(int poId, String status, Integer approvedBy) {
        String sql = "UPDATE purchase_orders SET status = ?, approved_by = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            if (approvedBy != null) {
                ps.setInt(2, approvedBy);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, poId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating status for PO: " + poId, e);
        }
    }

    /**
     * Marks a purchase order as RECEIVED with receipt date.
     *
     * @param conn         transaction connection
     * @param poId         purchase order ID
     * @param receivedDate date received
     * @return true if updated
     */
    public boolean markReceived(Connection conn, int poId, LocalDate receivedDate) throws SQLException {
        String sql = "UPDATE purchase_orders SET status = 'RECEIVED', received_date = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(receivedDate));
            ps.setInt(2, poId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a purchase order by ID (items cascade delete via foreign key).
     *
     * @param id purchase order ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM purchase_orders WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting purchase order: " + id, e);
        }
    }

    /**
     * Retrieves line items for a purchase order.
     *
     * @param conn connection to use
     * @param poId purchase order ID
     * @return list of items
     */
    public List<PurchaseOrderItem> getItemsByPoId(Connection conn, int poId) throws SQLException {
        String sql = "SELECT id, po_id, product_id, quantity, unit_price FROM purchase_order_items WHERE po_id = ?";
        List<PurchaseOrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, poId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrderItem item = new PurchaseOrderItem();
                    item.setId(rs.getInt("id"));
                    item.setPoId(rs.getInt("po_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * Inserts a single purchase order item.
     *
     * @param conn transaction connection
     * @param item item to insert
     * @return generated item ID
     */
    public int insertItem(Connection conn, PurchaseOrderItem item) throws SQLException {
        String sql = "INSERT INTO purchase_order_items (po_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getPoId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    item.setId(id);
                    return id;
                }
            }
        }
        return item.getId();
    }

    private PurchaseOrder mapResultSetToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(rs.getInt("id"));
        po.setSupplierId(rs.getInt("supplier_id"));
        po.setWarehouseId(rs.getInt("warehouse_id"));
        po.setStatus(rs.getString("status"));

        Date orderDate = rs.getDate("order_date");
        if (orderDate != null) {
            po.setOrderDate(orderDate.toLocalDate());
        }
        Date expectedDate = rs.getDate("expected_date");
        if (expectedDate != null) {
            po.setExpectedDate(expectedDate.toLocalDate());
        }
        Date receivedDate = rs.getDate("received_date");
        if (receivedDate != null) {
            po.setReceivedDate(receivedDate.toLocalDate());
        }

        po.setNotes(rs.getString("notes"));
        po.setCreatedBy(rs.getInt("created_by"));

        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            po.setApprovedBy(approvedBy);
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            po.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            po.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return po;
    }
}
