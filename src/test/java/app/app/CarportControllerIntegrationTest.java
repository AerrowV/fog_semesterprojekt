package app.app;

import io.javalin.http.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import app.persistence.MaterialMapper;
import app.persistence.CarportMapper;
import app.persistence.OrderMapper;
import app.persistence.ReceiptMapper;
import app.entities.Material;
import app.controllers.CarportController;
import static org.mockito.Mockito.*;

class CarportControllerIntegrationTest {

    private Context mockContext;
    private MockedStatic<CarportMapper> carportMapperMock;
    private MockedStatic<MaterialMapper> materialMapperMock;
    private MockedStatic<ReceiptMapper> receiptMapperMock;
    private MockedStatic<OrderMapper> orderMapperMock;
    private app.persistence.ConnectionPool connectionPool;

    @BeforeEach
    void setUp() {
        mockContext = mock(Context.class);

        carportMapperMock = mockStatic(CarportMapper.class);
        materialMapperMock = mockStatic(MaterialMapper.class);
        receiptMapperMock = mockStatic(ReceiptMapper.class);
        orderMapperMock = mockStatic(OrderMapper.class);
    }

    @Test
    void testSaveCustomerSpecificationsSuccess() throws Exception {

        when(mockContext.sessionAttribute("user_id")).thenReturn(1); // Simulate logged-in user
        when(mockContext.formParam("length")).thenReturn("300");
        when(mockContext.formParam("width")).thenReturn("400");
        when(mockContext.formParam("roof")).thenReturn("true");

        carportMapperMock.when(() -> CarportMapper.saveCarportSpecs(anyInt(), anyInt(), anyBoolean(), any()))
                .thenReturn(1); // Simulate carport spec save returning ID
        orderMapperMock.when(() -> OrderMapper.createOrder(any(), any()))
                .thenReturn(1); // Simulate order creation returning ID

        receiptMapperMock.when(() -> ReceiptMapper.saveReceiptPrice(anyInt(), anyDouble(), any()))
                .thenAnswer(invocation -> null); // Simulate successful execution of the void method

        materialMapperMock.when(() -> MaterialMapper.getMaterialById(anyInt(), any()))
                .thenReturn(new Material(1, "Test Material", 1, 1000, "test description","test unit",1000));

        CarportController.saveCustomerSpecifications(mockContext, connectionPool);


        verify(mockContext).attribute("message", "Carport specifications and order created successfully.");
        verify(mockContext).redirect("/orders");

        receiptMapperMock.verify(() -> ReceiptMapper.saveReceiptPrice(anyInt(), anyDouble(), any()), times(1));
    }




    @Test
    void testSaveCustomerSpecificationsNotLoggedIn() {

        when(mockContext.formParam("length")).thenReturn("300");
        when(mockContext.formParam("width")).thenReturn("400");
        when(mockContext.formParam("roof")).thenReturn("true");
        when(mockContext.sessionAttribute("user_id")).thenReturn(null);

        CarportController.saveCustomerSpecifications(mockContext, connectionPool);

        verify(mockContext).attribute("message", "You must be logged in to create a carport specification.");
        verify(mockContext).redirect("/login");
    }

    @Test
    void testSaveCustomerSpecificationsInvalidInput() {

        when(mockContext.formParam("length")).thenReturn("invalid");
        when(mockContext.formParam("width")).thenReturn("400");
        when(mockContext.formParam("roof")).thenReturn("true");
        when(mockContext.sessionAttribute("user_id")).thenReturn(1);


        CarportController.saveCustomerSpecifications(mockContext, connectionPool);

        verify(mockContext).attribute("message", "Invalid input. Please check your values.");
        verify(mockContext).render("choose-carport.html");
    }

    @AfterEach
    void tearDown() {
        carportMapperMock.close();
        materialMapperMock.close();
        receiptMapperMock.close();
        orderMapperMock.close();
    }

}
