package app.app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderMapperIntegrationTest {

    private ConnectionPool connectionPool;

    private int testUserId = 1;
    private int testCarportId = 1;
    private String testStatus = "Pending";

    @BeforeAll
    public void setUp() throws SQLException {
        connectionPool = ConnectionPool.getInstance("postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "testdb");

        try (Connection connection = connectionPool.getConnection()) {
            String createTablesSql = """
                        CREATE TABLE IF NOT EXISTS "user" (
                            user_id SERIAL PRIMARY KEY,
                            user_email VARCHAR(255) NOT NULL UNIQUE,
                            user_password VARCHAR(255) NOT NULL,
                            is_admin BOOLEAN DEFAULT FALSE
                        );
                    
                        CREATE TABLE IF NOT EXISTS carport_spec (
                            carport_id SERIAL PRIMARY KEY,
                            carport_length INT NOT NULL,
                            carport_width INT NOT NULL,
                            carport_roof BOOLEAN NOT NULL
                        );
                    
                        CREATE TABLE IF NOT EXISTS "order" (
                            order_id SERIAL PRIMARY KEY,
                            user_id INT NOT NULL REFERENCES "user"(user_id),
                            order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            order_status VARCHAR(50) NOT NULL,
                            carport_id INT NOT NULL REFERENCES carport_spec(carport_id)
                        );
                    
                        CREATE TABLE IF NOT EXISTS receipt (
                            receipt_id SERIAL PRIMARY KEY,
                            order_id INT UNIQUE NOT NULL REFERENCES "order"(order_id),
                            receipt_price DOUBLE PRECISION DEFAULT 0,
                            receipt_paid_date TIMESTAMP
                        );
                    """;

            try (PreparedStatement ps = connection.prepareStatement(createTablesSql)) {
                ps.execute();
            }
        }
    }


    @BeforeEach
    public void insertTestData() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            String insertUserSql = """
                        INSERT INTO "user" (user_id, user_email, user_password)
                        VALUES (1, 'testuser', 'password')
                        ON CONFLICT (user_id) DO NOTHING;
                    """;
            try (PreparedStatement ps = connection.prepareStatement(insertUserSql)) {
                ps.executeUpdate();
            }

            String insertCarportSql = """
                        INSERT INTO carport_spec (carport_id, carport_length, carport_width, carport_roof)
                        VALUES (1, 600, 400, true)
                        ON CONFLICT (carport_id) DO NOTHING;
                    """;
            try (PreparedStatement ps = connection.prepareStatement(insertCarportSql)) {
                ps.executeUpdate();
            }

            String insertOrderSql = """
                        INSERT INTO "order" (order_id, user_id, order_date, order_status, carport_id)
                        VALUES (1, 1, NOW(), 'Pending', 1)
                        ON CONFLICT (order_id) DO NOTHING;
                    """;
            try (PreparedStatement ps = connection.prepareStatement(insertOrderSql)) {
                ps.executeUpdate();
            }
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            String deleteDataSql = """
                        DELETE FROM "order";
                        DELETE FROM carport_spec;
                    """;
            try (PreparedStatement ps = connection.prepareStatement(deleteDataSql)) {
                ps.executeUpdate();
            }
        }
    }


    @Test
    public void testCreateOrder() throws DatabaseException {
        Order order = new Order(0, null, "Processing", 1, 1); // user_id = 1, carport_id = 1
        int orderId = OrderMapper.createOrder(order, connectionPool);
        assertTrue(orderId > 0, "Order ID should be greater than 0");

        Order retrievedOrder = OrderMapper.getOrderById(orderId, connectionPool);
        assertNotNull(retrievedOrder, "Retrieved order should not be null");
        assertEquals("Processing", retrievedOrder.getOrderStatus(), "Order status should match");
        assertEquals(1, retrievedOrder.getUserId(), "User ID should match");
    }


    @Test
    public void testGetOrderById() throws DatabaseException {
        Order order = OrderMapper.getOrderById(1, connectionPool);
        assertNotNull(order, "Order should not be null when retrieving by valid ID");
        assertEquals(testUserId, order.getUserId(), "User ID should match the inserted test order");
        assertEquals(testStatus, order.getOrderStatus(), "Order status should match the inserted test order");
    }

    @Test
    public void testGetAllOrders() throws DatabaseException {
        List<Order> orders = OrderMapper.getAllOrders(connectionPool);
        assertFalse(orders.isEmpty(), "Order list should not be empty");
        assertEquals(testUserId, orders.get(0).getUserId(), "User ID of first order should match the test order");
    }

    @Test
    public void testGetOrderByUserId() throws DatabaseException {
        List<Order> orders = OrderMapper.getOrderByUserId(testUserId, connectionPool);
        assertFalse(orders.isEmpty(), "Orders list for user should not be empty");
        assertEquals(testUserId, orders.get(0).getUserId(), "User ID of retrieved orders should match the test user");
    }

    @Test
    public void testUpdateOrderStatus() throws DatabaseException {
        OrderMapper.updateOrderStatus(1, "Approved", connectionPool);
        Order updatedOrder = OrderMapper.getOrderById(1, connectionPool);
        assertEquals("Approved", updatedOrder.getOrderStatus(), "Order status should be updated to 'Approved'");
    }


    @Test
    public void testCreateOrderWithInvalidUserId() {
        Order order = new Order(0, null, "Processing", -1, 2);
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            OrderMapper.createOrder(order, connectionPool);
        });
        assertEquals("Failed to create order: Invalid user ID.", exception.getMessage(), "Exception message should match expected");
    }
}
