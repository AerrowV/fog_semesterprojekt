package app.app.persistence;

import app.entities.Material;
import app.entities.MaterialSpec;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MaterialMapperIntegrationTest {

    private ConnectionPool connectionPool;
    private final int testMaterialId = 999;
    private final String testDescription = "Test Material";
    private final int testLength = 100;
    private final int testAmount = 50;
    private final String testUnit = "pcs";
    private final String testFunction = "Test Function";
    private final double testPrice = 200.0;
    private final int testCarportId = 1;
    private final int testOrderAmount = 10;

    @BeforeAll
    public void setUp() throws SQLException {
        connectionPool = ConnectionPool.getInstance("postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "testdb");

        try (Connection connection = connectionPool.getConnection()) {
            String createTablesSql = """
                CREATE TABLE IF NOT EXISTS material (
                    material_id SERIAL PRIMARY KEY,
                    material_description VARCHAR(255) UNIQUE NOT NULL,
                    material_length INT NOT NULL,
                    material_amount INT NOT NULL,
                    material_unit VARCHAR(50) NOT NULL,
                    material_function VARCHAR(255) NOT NULL,
                    material_price DOUBLE PRECISION NOT NULL
                );

                CREATE TABLE IF NOT EXISTS material_spec (
                    material_spec_id SERIAL PRIMARY KEY,
                    carport_id INT NOT NULL,
                    material_id INT NOT NULL REFERENCES material(material_id),
                    material_order_amount INT NOT NULL,
                    UNIQUE (carport_id, material_id)
                );
            """;
            try (PreparedStatement ps = connection.prepareStatement(createTablesSql)) {
                ps.execute();
            }
        }
    }

    @BeforeEach
    public void insertTestData() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {

            String insertCarportSpecSql = """
            INSERT INTO carport_spec (carport_id, carport_length, carport_width, carport_roof)
            VALUES (?, ?, ?, ?)
            ON CONFLICT DO NOTHING
        """;
            try (PreparedStatement ps = connection.prepareStatement(insertCarportSpecSql)) {
                ps.setInt(1, testCarportId);
                ps.setInt(2, 600);
                ps.setInt(3, 400);
                ps.setBoolean(4, true);
                ps.executeUpdate();
            }

            String insertMaterialSql = """
            INSERT INTO material (material_id, material_description, material_length, material_amount, material_unit, material_function, material_price)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT DO NOTHING
        """;
            try (PreparedStatement ps = connection.prepareStatement(insertMaterialSql)) {
                ps.setInt(1, testMaterialId);
                ps.setString(2, testDescription);
                ps.setInt(3, testLength);
                ps.setInt(4, testAmount);
                ps.setString(5, testUnit);
                ps.setString(6, testFunction);
                ps.setDouble(7, testPrice);
                ps.executeUpdate();
            }

            String insertMaterialSpecSql = """
            INSERT INTO material_spec (carport_id, material_id, material_order_amount)
            VALUES (?, ?, ?)
        """;
            try (PreparedStatement ps = connection.prepareStatement(insertMaterialSpecSql)) {
                ps.setInt(1, testCarportId);
                ps.setInt(2, testMaterialId);
                ps.setInt(3, testOrderAmount);
                ps.executeUpdate();
            }
        }
    }


    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            String deleteSql = """
                DELETE FROM material_spec WHERE material_id = ?;
                DELETE FROM material WHERE material_id = ?;
            """;
            try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                ps.setInt(1, testMaterialId);
                ps.setInt(2, testMaterialId);
                ps.executeUpdate();
            }
        }
    }

    @Test
    public void testGetAllMaterials() throws DatabaseException {
        List<Material> materials = MaterialMapper.getAllMaterials(connectionPool);
        assertFalse(materials.isEmpty(), "Materials list should not be empty");

        Material material = materials.stream().filter(m -> m.getMaterialId() == testMaterialId).findFirst().orElse(null);
        assertNotNull(material, "Inserted test material should be found in the list");
        assertEquals(testDescription, material.getDescription(), "Description should match the inserted material's description");
        assertEquals(testPrice, material.getPrice(), "Price should match the inserted material's price");
    }

    @Test
    public void testGetMaterialByIdSuccess() throws DatabaseException {
        Material material = MaterialMapper.getMaterialById(testMaterialId, connectionPool);
        assertNotNull(material, "Material should not be null when retrieving by valid ID");
        assertEquals(testDescription, material.getDescription(), "Description should match the retrieved material's description");
        assertEquals(testPrice, material.getPrice(), "Price should match the retrieved material's price");
    }

    @Test
    public void testGetMaterialByIdNonExistent() throws DatabaseException {
        int nonExistentId = 9999;
        Material material = MaterialMapper.getMaterialById(nonExistentId, connectionPool);
        assertNull(material, "Material should be null when retrieving with non-existent ID");
    }

    @Test
    public void testGetMaterialSpecsByCarportId() throws DatabaseException {
        List<MaterialSpec> specs = MaterialMapper.getMaterialSpecsByCarportId(testCarportId, connectionPool);
        assertFalse(specs.isEmpty(), "Material specs list should not be empty");
        MaterialSpec spec = specs.stream().filter(s -> s.getMaterialId() == testMaterialId).findFirst().orElse(null);
        assertNotNull(spec, "Inserted test material spec should be found in the list");
        assertEquals(testOrderAmount, spec.getMaterialOrderAmount(), "Order amount should match the inserted spec's order amount");
    }
}
