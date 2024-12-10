package app.app.persistence;

import app.entities.CarportSpec;
import app.entities.MaterialSpec;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import org.junit.jupiter.api.*;
import app.persistence.CarportMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
            String insertCarportSpecSql = """
                INSERT INTO carport_spec (carport_length, carport_width, carport_roof)
                VALUES (600, 300, true) RETURNING carport_id;
            """;
            String insertMaterialSql = """
                INSERT INTO material (material_description, material_length, material_amount, material_unit, material_function, material_price)
                VALUES ('Test Material', 100, 50, 'pcs', 'Test Function', 200);
            """;
            String insertMaterialSpecSql = """
                INSERT INTO material_spec (carport_id, material_id, material_order_amount)
                SELECT carport_id, material_id, 10 FROM carport_spec, material
                WHERE carport_spec.carport_length = 600 AND material.material_description = 'Test Material';
            """;
            try (PreparedStatement ps1 = connection.prepareStatement(insertCarportSpecSql)) {
                ps1.execute();
            }
            try (PreparedStatement ps2 = connection.prepareStatement(insertMaterialSql)) {
                ps2.execute();
            }
            try (PreparedStatement ps3 = connection.prepareStatement(insertMaterialSpecSql)) {
                ps3.execute();
            }
        }
    }

    @AfterEach
    void cleanTestData() throws Exception {
        try (Connection connection = connectionPool.getConnection()) {
            String deleteDataSql = "DELETE FROM material_spec; DELETE FROM material; DELETE FROM carport_spec;";
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
