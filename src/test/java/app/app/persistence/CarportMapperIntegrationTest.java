package app.app.persistence;

import app.entities.CarportSpec;
import app.entities.MaterialSpec;
import app.exceptions.DatabaseException;
import app.persistence.CarportMapper;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarportMapperIntegrationTest {

    private static ConnectionPool connectionPool;

    @BeforeAll
    static void setUpDatabase() throws Exception {

        connectionPool = app.persistence.ConnectionPool.getInstance("postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "testdb");

        try (Connection connection = connectionPool.getConnection()) {
            String createTablesSql = """
                        CREATE TABLE IF NOT EXISTS carport_spec (
                            carport_id SERIAL PRIMARY KEY,
                            carport_length INT NOT NULL,
                            carport_width INT NOT NULL,
                            carport_roof BOOLEAN NOT NULL
                        );
                    
                        CREATE TABLE IF NOT EXISTS material (
                            material_id SERIAL PRIMARY KEY,
                            material_description VARCHAR(255) NOT NULL,
                            material_length INT,
                            material_amount INT NOT NULL,
                            material_unit VARCHAR(50) NOT NULL,
                            material_function VARCHAR(255) NOT NULL,
                            material_price INT
                        );
                    
                        CREATE TABLE IF NOT EXISTS material_spec (
                            material_spec_id SERIAL PRIMARY KEY,
                            carport_id INT NOT NULL REFERENCES carport_spec(carport_id),
                            material_id INT NOT NULL REFERENCES material(material_id),
                            material_order_amount INT NOT NULL
                        );
                    """;
            try (PreparedStatement ps = connection.prepareStatement(createTablesSql)) {
                ps.execute();
            }
        }
    }

    @BeforeEach
    void insertTestData() throws Exception {
        try (Connection connection = connectionPool.getConnection()) {
            String cleanupSql = """
                        TRUNCATE TABLE material_spec RESTART IDENTITY CASCADE;
                        TRUNCATE TABLE material RESTART IDENTITY CASCADE;
                        TRUNCATE TABLE carport_spec RESTART IDENTITY CASCADE;
                    """;
            String insertCarportSpecSql = """
                        INSERT INTO carport_spec (carport_length, carport_width, carport_roof)
                        VALUES (600, 300, true) RETURNING carport_id;
                    """;

            String insertMaterialSql = """
                        INSERT INTO material (material_description, material_length, material_amount, material_unit, material_function, material_price)
                        VALUES  ('Test Material1', 100, 50, 'pcs', 'Test Function', 200),
                                ('Test Material2', 100, 50, 'pcs', 'Test Function', 200)
                        RETURNING material_id;
                    """;
            String insertMaterialSpecSql = """
                        INSERT INTO material_spec (carport_id, material_id, material_order_amount)
                        VALUES (?, ?, 10);
                    """;

            try (PreparedStatement cleanup = connection.prepareStatement(cleanupSql)) {
                cleanup.execute();
            }

            int carportId;
            try (PreparedStatement psCarport = connection.prepareStatement(insertCarportSpecSql);
                 ResultSet rs = psCarport.executeQuery()) {
                rs.next();
                carportId = rs.getInt("carport_id");
            }

            List<Integer> materialIds = new ArrayList<>();
            try (PreparedStatement psMaterial = connection.prepareStatement(insertMaterialSql);
                 ResultSet rs = psMaterial.executeQuery()) {
                while (rs.next()) {
                    materialIds.add(rs.getInt("material_id"));
                }
            }

            try (PreparedStatement psMaterialSpec = connection.prepareStatement(insertMaterialSpecSql)) {
                for (int materialId : materialIds) {
                    psMaterialSpec.setInt(1, carportId);
                    psMaterialSpec.setInt(2, materialId);
                    psMaterialSpec.executeUpdate();
                }
            }
        }
    }


    @AfterEach
    void cleanTestData() throws Exception {
        try (Connection connection = connectionPool.getConnection()) {
            String deleteDataSql = """
                        TRUNCATE TABLE \"order\" CASCADE;
                        TRUNCATE TABLE material_spec CASCADE;
                        TRUNCATE TABLE material CASCADE;
                        TRUNCATE TABLE carport_spec CASCADE;
                    """;
            try (PreparedStatement ps = connection.prepareStatement(deleteDataSql)) {
                ps.execute();
            }
        }
    }

    @Test
    void testSaveCarportSpecs() throws DatabaseException {
        int carportId = CarportMapper.saveCarportSpecs(500, 400, false, connectionPool);
        assertTrue(carportId > 0);
    }

    @Test
    void testGetCarportId() throws DatabaseException {
        int carportId = CarportMapper.getCarportId(600, 300, true, connectionPool);
        assertTrue(carportId > 0);
    }

    @Test
    void testSaveMaterialSpec() throws DatabaseException {
        int carportId = CarportMapper.getCarportId(600, 300, true, connectionPool);
        CarportMapper.saveMaterialSpec(carportId, 1, 10, connectionPool);
        List<MaterialSpec> specs = MaterialMapper.getMaterialSpecsByCarportId(carportId, connectionPool);

        assertFalse(specs.isEmpty(), "Material specs should not be empty");
        assertEquals(10, specs.get(0).getMaterialOrderAmount());
    }


    @Test
    void testGetCarportSpecsById() throws DatabaseException {
        int carportId = CarportMapper.getCarportId(600, 300, true, connectionPool);
        CarportSpec spec = CarportMapper.getCarportSpecsById(carportId, connectionPool);
        assertNotNull(spec);
        assertEquals(600, spec.getLength());
        assertEquals(300, spec.getWidth());
        assertTrue(spec.isRoofType());
    }
}
