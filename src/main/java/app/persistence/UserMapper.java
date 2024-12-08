package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from \"user\" where user_email = ? and user_password = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    boolean isAdmin = rs.getBoolean("is_admin");
                    int id = rs.getInt("user_id");
                    return new User(id,email,password,isAdmin);
                } else {
                    throw new DatabaseException("Error in login. Try again");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into \"user\" (user_email, user_password) values (?, ?)";
        

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, password);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DatabaseException("Error creating user");
                }
            }
        } catch (SQLException e) {
            String msg = "Error creating user";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "E-mail already exists";
            }
            throw new DatabaseException(msg);
        }
    }

    public static boolean checkEmail(String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT user_email FROM \"user\" WHERE user_email = ?";


        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();

                return rs.next();
            }
        } catch (SQLException e) {
            String msg = "Database error occurred while checking the email.";
            throw new DatabaseException(msg);
        }
    }

    public static boolean checkZipCode(int zipCode, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT zip_code FROM zip_code WHERE zip_code = ?";


        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, zipCode);
                ResultSet rs = ps.executeQuery();

                return rs.next();
            }
        } catch (SQLException e) {
            String msg = "Database error occurred while checking the zipCode.";
            throw new DatabaseException(msg);
        }
    }

    public static void saveUserDataToDB(String email, String firstName, String lastName, String streetName, String houseNumber, int zip, ConnectionPool connectionPool) throws DatabaseException {
        String updateUserSql = "UPDATE \"user\" SET first_name = ?, last_name = ?, address_id = ? WHERE user_email = ?";
        String findZipCodeSql = "SELECT zip_code FROM zip_code WHERE zip_code = ?";
        String insertAddressSql = """
                INSERT INTO address (street_name, house_number, zip_code) 
                VALUES (?, ?, ?) 
                RETURNING address_id
            """;

        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement zipStmt = connection.prepareStatement(findZipCodeSql)) {
                zipStmt.setInt(1, zip);
                try (ResultSet rs = zipStmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("Invalid ZIP code provided.");
                    }
                }
            }

            int addressId;
            try (PreparedStatement addressStmt = connection.prepareStatement(insertAddressSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                addressStmt.setString(1, streetName);
                addressStmt.setString(2, houseNumber);
                addressStmt.setInt(3, zip);
                addressStmt.executeUpdate();

                try (ResultSet rs = addressStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        addressId = rs.getInt("address_id");
                    } else {

                        String findAddressSql = "SELECT address_id FROM address WHERE street_name = ? AND house_number = ? AND zip_code = ?";
                        try (PreparedStatement findStmt = connection.prepareStatement(findAddressSql)) {
                            findStmt.setString(1, streetName);
                            findStmt.setString(2, houseNumber);
                            findStmt.setInt(3, zip);
                            try (ResultSet findRs = findStmt.executeQuery()) {
                                if (findRs.next()) {
                                    addressId = findRs.getInt("address_id");
                                } else {
                                    throw new DatabaseException("Unable to find or insert address.");
                                }
                            }
                        }
                    }
                }
            }

            try (PreparedStatement userStmt = connection.prepareStatement(updateUserSql)) {
                userStmt.setString(1, firstName);
                userStmt.setString(2, lastName);
                userStmt.setInt(3, addressId);
                userStmt.setString(4, email);

                int rowsAffected = userStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DatabaseException("Error saving user data. Email not found.");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Error saving data: " + e.getMessage());
        }
    }

    public static User getUserByIdWithAddress(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = """
        SELECT 
            u.user_id, 
            u.user_email, 
            u.first_name, 
            u.last_name, 
            a.street_name, 
            a.house_number, 
            z.zip_code, 
            z.city 
        FROM "user" u
        JOIN address a ON u.address_id = a.address_id
        JOIN zip_code z ON a.zip_code = z.zip_code
        WHERE u.user_id = ?;
        """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("user_email"),
                        null,
                        null,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("user_id")
                );

                String fullAddress = String.format("%s %s, %d %s",
                        rs.getString("street_name"),
                        rs.getString("house_number"),
                        rs.getInt("zip_code"),
                        rs.getString("city")
                );

                user.setFullAddress(fullAddress);

                return user;
            } else {
                throw new DatabaseException("User not found for ID: " + userId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user details: " + e.getMessage());
        }
    }

}
