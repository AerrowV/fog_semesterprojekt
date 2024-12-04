package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMapper {

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
}
