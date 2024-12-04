package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static int createOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        if (order.getUserId() <= 0) {
            throw new DatabaseException("Failed to create order: Invalid user ID.");
        }

        String orderSql = "INSERT INTO \"order\" (user_id, order_date, order_status, carport_id) VALUES (?, CURRENT_TIMESTAMP, ?, ?) RETURNING order_id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(orderSql)) {

            ps.setInt(1, order.getUserId());
            ps.setString(2, order.getOrderStatus());
            ps.setInt(3, order.getCarportId());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int orderId = rs.getInt("order_id");

                createReceipt(orderId, connectionPool);

                return orderId;
            } else {
                throw new DatabaseException("Failed to create order: No ID returned.");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key constraint")) {
                throw new DatabaseException("Failed to create order: User or carport does not exist.");
            }
            throw new DatabaseException("Database error while creating order: " + e.getMessage());
        }
    }

    public static Order getOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM \"order\" WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Order(
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        rs.getString("order_status"),
                        rs.getInt("user_id"),
                        rs.getInt("carport_id")
                );
            } else {
                throw new DatabaseException("Order not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving order: " + e.getMessage());
        }
    }

    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM \"order\"";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        rs.getString("order_status"),
                        rs.getInt("user_id"),
                        rs.getInt("carport_id")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all orders: " + e.getMessage());
        }
        return orders;
    }

    public static List<Order> getOrderByUserId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM \"order\" WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        rs.getString("order_status"),
                        rs.getInt("user_id"),
                        rs.getInt("carport_id")
                ));
            }

            if (orders.isEmpty()) {
                throw new DatabaseException("No orders found for this user.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving orders: " + e.getMessage());
        }
        return orders;
    }

    public static void updateOrderStatus(int orderId, String newStatus, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE \"order\" SET order_status = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Order not found or not updated.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating order status: " + e.getMessage());
        }
    }

    public static void updateOrderPrice(int orderId, double price, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE receipt SET receipt_price = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, price);
            ps.setInt(2, orderId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Error updating order price: " + e.getMessage());
        }
    }

    private static void createReceipt(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String receiptSql = "INSERT INTO receipt (order_id, receipt_price, receipt_paid_date) VALUES (?, 0, NULL)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(receiptSql)) {

            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create receipt: No rows affected.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error creating receipt: " + e.getMessage());
        }
    }
}

