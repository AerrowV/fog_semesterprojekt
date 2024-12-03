package app.persistence;

import app.controllers.PaymentController;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentMapper {

    public static void saveUserDataToDB(String firstName, String lastName, String address, int zipCode, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO \"user\" (first_name, last_name, address, zip_code) VALUES (?, ?, ?, ?)";


        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, address);
                ps.setInt(4, zipCode);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DatabaseException("Error saving data");
                }
            }
        } catch (SQLException e) {
            String msg = "Error saving data";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "E-mail already exists";
            }
            throw new DatabaseException(msg);
        }
    }

}
