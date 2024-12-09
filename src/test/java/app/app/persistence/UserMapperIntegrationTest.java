package app.app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserMapperIntegrationTest {

    private app.persistence.ConnectionPool connectionPool;

    @BeforeAll
    void setup() throws SQLException {
        connectionPool = ConnectionPool.getInstance("postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "testdb");

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS "user" (
                        user_id SERIAL PRIMARY KEY,
                        user_email VARCHAR(255) UNIQUE NOT NULL,
                        user_password VARCHAR(255) NOT NULL,
                        is_admin BOOLEAN DEFAULT FALSE,
                        first_name VARCHAR(255),
                        last_name VARCHAR(255),
                        address_id INTEGER
                    );
                    
                    CREATE TABLE IF NOT EXISTS address (
                        address_id SERIAL PRIMARY KEY,
                        street_name VARCHAR(255),
                        house_number VARCHAR(255),
                        zip_code INTEGER
                    );

                    CREATE TABLE IF NOT EXISTS zip_code (
                        zip_code INTEGER PRIMARY KEY,
                        city VARCHAR(255)
                    );
            """)) {
                stmt.executeUpdate();
            }
        }
    }

    @BeforeEach
    void cleanUp() throws SQLException {
        // Clean test data
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("TRUNCATE \"user\" RESTART IDENTITY CASCADE")) {
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void testCreateUserAndLogin() throws DatabaseException {
        String email = "testuser@example.com";
        String password = "password123";

        // Test createUser
        UserMapper.createUser(email, password, connectionPool);
        assertTrue(UserMapper.checkEmail(email, connectionPool));

        // Test login
        User user = UserMapper.login(email, password, connectionPool);
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void testCheckInvalidEmail() throws DatabaseException {
        String email = "nonexistent@example.com";
        assertFalse(UserMapper.checkEmail(email, connectionPool));
    }

    @Test
    void testGetUserByIdWithAddress() throws DatabaseException, SQLException {
        // Insert ZIP code
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement zipStmt = connection.prepareStatement("INSERT INTO zip_code (zip_code, city) VALUES (12345, 'Test City')")) {
            zipStmt.executeUpdate();
        }

        // Insert address and user
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement addrStmt = connection.prepareStatement("INSERT INTO address (street_name, house_number, zip_code) VALUES ('Main St', '42A', 12345) RETURNING address_id");
             PreparedStatement userStmt = connection.prepareStatement("INSERT INTO \"user\" (user_email, user_password, first_name, last_name, address_id) VALUES ('test@example.com', 'password', 'John', 'Doe', ?)");) {

            int addressId;
            try (var rs = addrStmt.executeQuery()) {
                assertTrue(rs.next());
                addressId = rs.getInt("address_id");
            }

            userStmt.setInt(1, addressId);
            userStmt.executeUpdate();
        }

        // Retrieve user by ID
        User user = UserMapper.getUserByIdWithAddress(1, connectionPool);
        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Main St 42A, 12345 Test City", user.getFullAddress());
    }

    @AfterAll
    void teardown() throws SQLException {
        // Drop test tables
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("""
                    DROP TABLE IF EXISTS "user" CASCADE;
                    DROP TABLE IF EXISTS address CASCADE;
                    DROP TABLE IF EXISTS zip_code CASCADE;
            """)) {
                stmt.executeUpdate();
            }
        }
    }
}
