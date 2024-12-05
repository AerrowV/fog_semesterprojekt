package app.persistence;

import app.controllers.CarportController;
import app.entities.Receipt;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
