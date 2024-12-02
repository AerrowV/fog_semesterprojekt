package app.persistence;

import app.entities.CarportSpec;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarportMapper {

    public static void saveCarportSpecs(int length, int width, boolean hasRoof, ConnectionPool connectionPool) throws DatabaseException {
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

    public static void saveMaterial_spec(int carportId, int material_id, int material_order_amount, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into material_spec (carport_id, material_id, material_order_amount) values (?, ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, carportId);
                ps.setInt(2, material_id);
                ps.setInt(3, material_order_amount);
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

    public static CarportSpec getCarportSpecsById(int carportId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from carport_spec where carport_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new CarportSpec(
                        rs.getInt("carport_id"),
                        rs.getInt("carport_length"),
                        rs.getInt("carport_width"),
                        rs.getBoolean("carport_roof")
                );
            } else {
                throw new DatabaseException("Specs not found.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving specs: " + e.getMessage());
        }
    }
}
