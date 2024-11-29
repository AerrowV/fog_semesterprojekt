package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CarportMapper {

    public static void carportSpecs(int length, int width, boolean hasRoof, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into carport_spec (carport_length, carport_width, carport_roof) values (?, ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, length);
                ps.setInt(2, width);
                ps.setBoolean(3, hasRoof);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DatabaseException("Error");
                }
            }
        } catch (SQLException e) {
            String msg = "Error";
            if (e.getMessage().startsWith("ERROR: duplicate key value")) {
                msg = "dublicate key";
            }
            throw new DatabaseException(msg);
        }
    }
}
