package app.persistence;

import app.entities.Receipt;
import app.exceptions.DatabaseException;

import java.sql.*;

public class ReceiptMapper {

    public static double getReceiptPriceByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT receipt_price FROM receipt WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("receipt_price");
            } else {
                throw new DatabaseException("Receipt not found for order ID: " + orderId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving receipt price: " + e.getMessage());
        }
    }

    public static void saveReceiptPrice(int orderId, double price, ConnectionPool connectionPool) throws DatabaseException {
        price = price * 1.4;
        String sql = "UPDATE receipt SET receipt_price = ? WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, price);
            ps.setInt(2, orderId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to update receipt price: No receipt found for order ID " + orderId);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error updating receipt price: " + e.getMessage());
        }
    }

    public static void updatePaidDate(int orderId, Timestamp paidDate, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE receipt SET receipt_paid_date = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, paidDate);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update paid date: " + e.getMessage());
        }
    }

    public static Timestamp getReceiptPaidDate(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT receipt_paid_date FROM receipt WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getTimestamp("receipt_paid_date");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch receipt paid date: " + e.getMessage());
        }
    }

    public static Receipt getReceiptByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM receipt WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Receipt(
                        rs.getInt("receipt_id"),
                        rs.getTimestamp("receipt_paid_date"),
                        rs.getInt("receipt_price"),
                        rs.getInt("order_id")
                );
            } else {
                throw new DatabaseException("Receipt not found for Order ID: " + orderId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving receipt: " + e.getMessage());
        }
    }
}

