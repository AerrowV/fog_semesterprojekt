package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShedMapper {

    public static void shedSpecs(int length, int width, int carportId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into shed_spec (shed_length, shed_width, carport_id) values (?, ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, length);
                ps.setInt(2, width);
                ps.setInt(3, carportId);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DatabaseException("Error");
                }
            }
        } catch (SQLException e) {
            String msg = "Error";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "dublicate key";
            }
            throw new DatabaseException(msg);
        }
    }
}

