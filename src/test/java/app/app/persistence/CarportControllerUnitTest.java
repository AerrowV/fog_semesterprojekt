package app.app.persistence;

import app.controllers.CarportController;
import app.entities.Material;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CarportControllerUnitTest {

    @Mock
    private ConnectionPool mockConnectionPool;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUnderFasciaBoardFrontandBack() throws DatabaseException {
        double width = 300;
        double length = 400;
        Material expectedMaterial = new Material(1, "understernbrædder til for & bag ende", 100, 150, "pcs", "Test Material", 75);

        try (MockedStatic<MaterialMapper> mockedStatic = mockStatic(MaterialMapper.class)) {
            mockedStatic.when(() -> MaterialMapper.getMaterialById(1, mockConnectionPool)).thenReturn(expectedMaterial);

            Material result = CarportController.underFasciaBoardFrontandBack(length, width, mockConnectionPool);

            assertNotNull(result);
            assertEquals(1, result.getMaterialId());
            assertEquals("understernbrædder til for & bag ende", result.getDescription());
            assertEquals(2, result.getAmount());
            assertEquals(150.0, result.getPrice());
        }
    }

    @Test
    void testUnderFasciaBoardSides() throws DatabaseException {
        double width = 400;
        double length = 300;
        Material expectedMaterial = new Material(1, "understernbrædder til siderne", 100, 150, "pcs", "Test Material", 75);

        try (MockedStatic<MaterialMapper> mockedStatic = mockStatic(MaterialMapper.class)) {
            mockedStatic.when(() -> MaterialMapper.getMaterialById(1, mockConnectionPool)).thenReturn(expectedMaterial);

            Material result = CarportController.underFasciaBoardSides(length, width, mockConnectionPool);

            assertNotNull(result);
            assertEquals(1, result.getMaterialId());
            assertEquals("understernbrædder til siderne", result.getDescription());
            assertEquals(2, result.getAmount());
            assertEquals(150.0, result.getPrice());
        }
    }


    @Test
    void testOverFasciaBoardFront() throws DatabaseException {
        double width = 360;
        double length = 400;
        Material expectedMaterial = new Material(8, "oversternbrædder til forenden", 100, 150, "pcs", "Test Material", 150);

        try (MockedStatic<MaterialMapper> mockedStatic = mockStatic(MaterialMapper.class)) {
            mockedStatic.when(() -> MaterialMapper.getMaterialById(8, mockConnectionPool)).thenReturn(expectedMaterial);

            Material result = CarportController.overFasciaBoardFront(length, width, mockConnectionPool);

            assertNotNull(result);
            assertEquals(8, result.getMaterialId());
            assertEquals("oversternbrædder til forenden", result.getDescription());
            assertEquals(1, result.getAmount());
            assertEquals(150.0, result.getPrice());
        }
    }

    @Test
    void testCarportStykListe() throws DatabaseException {
        double width = 400;
        double length = 420;
        Material mockMaterial = new Material(1, "Test Material", 100, 150, "pcs", "Test Description", 75);

        try (MockedStatic<MaterialMapper> mockedStatic = mockStatic(MaterialMapper.class)) {
            mockedStatic.when(() -> MaterialMapper.getMaterialById(anyInt(), eq(mockConnectionPool))).thenReturn(mockMaterial);

            var result = CarportController.carportStykListe(length, width, 1, mockConnectionPool);

            assertNotNull(result);
            assertEquals(9, result.size());
            assertEquals("understernbrædder til siderne", result.get(0).getDescription());
        }
    }
}
