package app.persistence;
import app.entities.Material;
import app.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


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
                ResultSet rs = ps.executeQuery();

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

