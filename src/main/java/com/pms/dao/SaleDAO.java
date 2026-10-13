package com.pms.dao;

import com.ims.common.db.DatabaseManager;
import com.pms.model.Sale;
import com.pms.model.SaleItem;

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
 * Data Access Object for 'sales' and 'sale_items' using standard JDBC PreparedStatement.
 */
public class SaleDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Finds a sale by ID, including line items.
     *
     * @param id sale ID
     * @return Optional Sale
     */
    public Optional<Sale> findById(int id) {
        String sql = "SELECT id, invoice_number, warehouse_id, customer_name, customer_phone, total_amount, " +
                     "sale_date, created_by FROM sales WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sale sale = mapResultSetToSale(rs);
                    sale.setItems(getItemsBySaleId(conn, sale.getId()));
                    return Optional.of(sale);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sale by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Finds a sale by unique invoice number, including line items.
     *
     * @param invoiceNumber invoice number
     * @return Optional Sale
     */
    public Optional<Sale> findByInvoiceNumber(String invoiceNumber) {
        String sql = "SELECT id, invoice_number, warehouse_id, customer_name, customer_phone, total_amount, " +
                     "sale_date, created_by FROM sales WHERE invoice_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sale sale = mapResultSetToSale(rs);
                    sale.setItems(getItemsBySaleId(conn, sale.getId()));
                    return Optional.of(sale);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sale by invoice number: " + invoiceNumber, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all sales ordered by sale date descending.
     *
     * @return list of sales
     */
    public List<Sale> findAll() {
        String sql = "SELECT id, invoice_number, warehouse_id, customer_name, customer_phone, total_amount, " +
                     "sale_date, created_by FROM sales ORDER BY sale_date DESC, id DESC";
        List<Sale> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Sale sale = mapResultSetToSale(rs);
                sale.setItems(getItemsBySaleId(conn, sale.getId()));
                list.add(sale);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all sales", e);
        }
        return list;
    }

    /**
     * Retrieves sales within a given date range.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of sales
     */
    public List<Sale> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT id, invoice_number, warehouse_id, customer_name, customer_phone, total_amount, " +
                     "sale_date, created_by FROM sales WHERE DATE(sale_date) BETWEEN ? AND ? ORDER BY sale_date DESC";
        List<Sale> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale sale = mapResultSetToSale(rs);
                    sale.setItems(getItemsBySaleId(conn, sale.getId()));
                    list.add(sale);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sales by date range", e);
        }
        return list;
    }

    /**
     * Inserts a sale record and its items in a transaction.
     *
     * @param sale sale to insert
     * @return generated sale ID
     */
    public int insert(Sale sale) {
        String sql = "INSERT INTO sales (invoice_number, warehouse_id, customer_name, customer_phone, total_amount, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sale.getInvoiceNumber());
                ps.setInt(2, sale.getWarehouseId());
                ps.setString(3, sale.getCustomerName());
                ps.setString(4, sale.getCustomerPhone());
                ps.setBigDecimal(5, sale.getTotalAmount());
                ps.setInt(6, sale.getCreatedBy());

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int saleId = rs.getInt(1);
                        sale.setId(saleId);
                    }
                }

                if (sale.getItems() != null && !sale.getItems().isEmpty()) {
                    for (SaleItem item : sale.getItems()) {
                        item.setSaleId(sale.getId());
                        insertItem(conn, item);
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return sale.getId();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting sale: " + sale.getInvoiceNumber(), e);
        }
    }

    /**
     * Updates an existing sale header.
     *
     * @param sale sale with updated data
     * @return true if updated
     */
    public boolean update(Sale sale) {
        String sql = "UPDATE sales SET warehouse_id = ?, customer_name = ?, customer_phone = ?, " +
                     "total_amount = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sale.getWarehouseId());
            ps.setString(2, sale.getCustomerName());
            ps.setString(3, sale.getCustomerPhone());
            ps.setBigDecimal(4, sale.getTotalAmount());
            ps.setInt(5, sale.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating sale: " + sale.getId(), e);
        }
    }

    /**
     * Deletes a sale by ID (items cascade delete via foreign key).
     *
     * @param id sale ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM sales WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting sale: " + id, e);
        }
    }

    /**
     * Retrieves line items for a sale.
     *
     * @param conn connection to use
     * @param saleId sale ID
     * @return list of items
     */
    public List<SaleItem> getItemsBySaleId(Connection conn, int saleId) throws SQLException {
        String sql = "SELECT id, sale_id, product_id, quantity, unit_price FROM sale_items WHERE sale_id = ?";
        List<SaleItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SaleItem item = new SaleItem();
                    item.setId(rs.getInt("id"));
                    item.setSaleId(rs.getInt("sale_id"));
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
     * Inserts a sale line item.
     *
     * @param conn transaction connection
     * @param item item to insert
     * @return generated item ID
     */
    public int insertItem(Connection conn, SaleItem item) throws SQLException {
        String sql = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getSaleId());
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

    private Sale mapResultSetToSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getInt("id"));
        sale.setInvoiceNumber(rs.getString("invoice_number"));
        sale.setWarehouseId(rs.getInt("warehouse_id"));
        sale.setCustomerName(rs.getString("customer_name"));
        sale.setCustomerPhone(rs.getString("customer_phone"));
        sale.setTotalAmount(rs.getBigDecimal("total_amount"));

        Timestamp ts = rs.getTimestamp("sale_date");
        if (ts != null) {
            sale.setSaleDate(ts.toLocalDateTime());
        }

        sale.setCreatedBy(rs.getInt("created_by"));
        return sale;
    }
}
