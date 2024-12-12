package app.persistence;

import app.entities.Material;
import app.entities.MaterialSpec;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MaterialMapper {

    public static void createMaterial(Material material, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO material (material_description, material_length, material_amount, material_unit, material_function, material_price) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, material.getDescription());
            ps.setInt(2, material.getLength());
            ps.setInt(3, material.getAmount());
            ps.setString(4, material.getUnit());
            ps.setString(5, material.getFunction());
            ps.setInt(6, material.getPrice());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Error creating material.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create material: " + e.getMessage());
        }

//        public class MaterialDAO {
//            public static List<Material> getMaterials(ConnectionPool connectionPool) throws DatabaseException {
//                String sql = "SELECT id, name, length, width, height FROM materiale";
//                List<Material> materials = new ArrayList<>();
//
//                try (Connection connection = connectionPool.getConnection();
//                     PreparedStatement ps = connection.prepareStatement(sql);
//                     ResultSet rs = ps.executeQuery()) {
//
//                    while (rs.next()) {
//                        materials.add(new Material(
//                                rs.getInt("materiale_id"),
//                                rs.getString("material_description"),
//                                rs.getInt("materiale_length"),
//                                rs.getInt("materiale_amount"),
//                                rs.getString("materiale_unit"),
//                                rs.getString("materiale_function"),
//                                rs.getInt("materiale_price")
//
//                        ));
//                    }
//                } catch (SQLException e) {
//                    throw new DatabaseException("Error retrieving materials: " + e.getMessage());
//                }
//                return materials;
//            }
//        }
    }

    public static List<Material> getAllMaterials(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM material";
        List<Material> materials = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                materials.add(new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_description"),
                        rs.getInt("material_length"),
                        rs.getInt("material_amount"),
                        rs.getString("material_unit"),
                        rs.getString("material_function"),
                        rs.getInt("material_price")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch materials: " + e.getMessage());
        }
        return materials;
    }

    public static Material getMaterialById(int materialId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM material WHERE material_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Material(
                            rs.getInt("material_id"),
                            rs.getString("material_description"),
                            rs.getInt("material_length"),
                            rs.getInt("material_amount"),
                            rs.getString("material_unit"),
                            rs.getString("material_function"),
                            rs.getInt("material_price")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching material by ID: " + materialId);
        }
        return null;
    }


    public static Material getMaterialSetAmount(int materialId, int amount, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM material WHERE material_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_description"),
                        rs.getInt("material_length"),
                        rs.getInt(amount),
                        rs.getString("material_unit"),
                        rs.getString("material_function"),
                        rs.getInt("material_price")
                );
            } else {
                throw new DatabaseException("Material not found");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch material: " + e.getMessage());
        }
    }

    public static void updateMaterial(Material material, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE material SET material_description = ?, material_length = ?, material_amount = ?, material_unit = ?, material_function = ?, material_price = ? WHERE material_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, material.getDescription());
            ps.setInt(2, material.getLength());
            ps.setInt(3, material.getAmount());
            ps.setString(4, material.getUnit());
            ps.setString(5, material.getFunction());
            ps.setInt(6, material.getPrice());
            ps.setInt(7, material.getMaterialId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to update material");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update material: " + e.getMessage());
        }
    }

    public static void deleteMaterial(int materialId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM material WHERE material_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, materialId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to delete material");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete material: " + e.getMessage());
        }
    }

    public static List<MaterialSpec> getMaterialSpecsByCarportId(int carportId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM material_spec WHERE carport_id = ?";
        List<MaterialSpec> materialSpecs = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                materialSpecs.add(new MaterialSpec(
                        rs.getInt("material_spec_id"),
                        rs.getInt("carport_id"),
                        rs.getInt("material_id"),
                        rs.getInt("material_order_amount")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving material specs: " + e.getMessage());
        }
        return materialSpecs;
    }

    public static void updateMaterialAmount(int materialId, int amount, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE material SET material_amount = material_amount - ? WHERE material_id = ? AND material_amount + ? >= 0";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, amount);
            ps.setInt(2, materialId);
            ps.setInt(3, amount);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to update material amount");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Failed to update material amount: " + e.getMessage());
        }
    }
}

