package app.persistence;

import app.entities.CarportSpec;
import app.exceptions.DatabaseException;

import java.sql.*;

public class CarportMapper {

    public static int saveCarportSpecs(int length, int width, boolean hasRoof, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO carport_spec (carport_length, carport_width, carport_roof) VALUES (?, ?, ?)";
        int carportId = 0;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, length);
            ps.setInt(2, width);
            ps.setBoolean(3, hasRoof);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating carport failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    carportId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating carport failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }
        return carportId;
    }

    public static int getCarportId(int length, int width, boolean hasRoof, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT carport_id FROM carport_spec WHERE carport_length = ? AND carport_width = ? AND carport_roof = ?";
        int carportId = 0;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, length);
            ps.setInt(2, width);
            ps.setBoolean(3, hasRoof);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    carportId = rs.getInt("carport_id");
                } else {
                    throw new SQLException("No carport found with the given specifications.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }
        return carportId;
    }

    public static void saveMaterialSpec(int carportId, int material_id, int material_order_amount, ConnectionPool connectionPool) throws DatabaseException {
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
