package app.persistence;

import app.controllers.PaymentController;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentMapper {

    public static void saveUserDataToDB(String email, String firstName, String lastName, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE \"user\" SET first_name = ?, last_name = ? WHERE user_email = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
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
