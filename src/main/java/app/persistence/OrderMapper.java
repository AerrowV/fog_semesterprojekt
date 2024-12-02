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

        String sql = "INSERT INTO orders (user_id, order_date, order_status) VALUES (?, CURRENT_DATE, ?) RETURNING order_id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, order.getUserId());
            ps.setString(2, order.getOrderStatus());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("order_id");
            } else {
                throw new DatabaseException("Failed to create order: No ID returned.");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key constraint")) {
                throw new DatabaseException("Failed to create order: User does not exist.");
            }
            throw new DatabaseException("Database error while creating order: " + e.getMessage());
        }
    }


    public static Order getOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Order(
                        rs.getInt("order_id"),
                        rs.getString("order_date"),
                        rs.getString("order_status"),
                        rs.getInt("user_id")
                );
            } else {
                throw new DatabaseException("Order not found.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving order: " + e.getMessage());
        }
    }

    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("order_date"),
                        rs.getString("order_status"),
                        rs.getInt("user_id")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all orders: " + e.getMessage());
        }
        return orders;
    }

    public static void saveStykliste(ConnectionPool connectionPool, Order order) throws DatabaseException {
        String sql = "INSERT INTO material_spec (user_id, order_date, order_status) VALUES (?, CURRENT_DATE, ?) RETURNING order_id";



    }
}
