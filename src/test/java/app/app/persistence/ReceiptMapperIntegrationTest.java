package app.persistence;

import app.entities.Receipt;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReceiptMapperIntegrationTest {

    private ConnectionPool connectionPool;
    private int testReceiptId = 999;
    private int testOrderId = 1;
    private int testPrice = 100;

    @BeforeAll
    public void setUp() throws SQLException {
        connectionPool = ConnectionPool.getInstance("postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "testdb");

        // Ensure required order exists
        try (Connection connection = connectionPool.getConnection()) {
            String createOrderSql = """
                INSERT INTO "order" (order_id, order_date, order_status, user_id, carport_id)
                VALUES (?, NOW(), 'Pending', NULL, NULL)
                ON CONFLICT (order_id) DO NOTHING;
            """;
            try (PreparedStatement ps = connection.prepareStatement(createOrderSql)) {
                ps.setInt(1, testOrderId);
                ps.executeUpdate();
            }
        }
    }

    @BeforeEach
    public void insertTestData() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            String insertSql = """
                INSERT INTO receipt (receipt_id, receipt_price, receipt_paid_date, order_id)
                VALUES (?, ?, NULL, ?)
                ON CONFLICT (receipt_id) DO NOTHING;
            """;
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setInt(1, testReceiptId);
                ps.setInt(2, testPrice);
                ps.setInt(3, testOrderId);
                ps.executeUpdate();
            }
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            String deleteSql = "DELETE FROM receipt WHERE receipt_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                ps.setInt(1, testReceiptId);
                ps.executeUpdate();
            }
        }
    }

    @Test
    public void testGetReceiptByOrderId() throws DatabaseException {
        Receipt receipt = ReceiptMapper.getReceiptByOrderId(testOrderId, connectionPool);
        assertNotNull(receipt, "Receipt should not be null when retrieving by valid order ID");
        assertEquals(testPrice, receipt.getPrice(), "Price should match the inserted receipt's price");
        assertEquals(testOrderId, receipt.getOrderId(), "Order ID should match the inserted receipt's order ID");
        assertNull(receipt.getPaidDate(), "Paid date should be null for the inserted receipt");
    }

    @Test
    public void testUpdatePaidDate() throws DatabaseException, SQLException {
        Timestamp now = Timestamp.from(Instant.now());
        ReceiptMapper.updatePaidDate(testOrderId, now, connectionPool);

        Timestamp paidDate = ReceiptMapper.getReceiptPaidDate(testOrderId, connectionPool);
        assertNotNull(paidDate, "Paid date should not be null after update");
        assertEquals(now, paidDate, "Paid date should match the updated value");
    }

    @Test
    public void testSaveReceiptPrice() throws DatabaseException {
        int newPrice = 200;
        ReceiptMapper.saveReceiptPrice(testOrderId, newPrice, connectionPool);

        double updatedPrice = ReceiptMapper.getReceiptPriceByOrderId(testOrderId, connectionPool);
        assertEquals(newPrice, updatedPrice, "Price should match the updated value");
    }

    @Test
    public void testGetReceiptByOrderIdNonExistent() throws DatabaseException {
        int nonExistentOrderId = 9999;
        Receipt receipt = ReceiptMapper.getReceiptByOrderId(nonExistentOrderId, connectionPool);
        assertNull(receipt, "Receipt should be null when retrieving with non-existent order ID");
    }
}
