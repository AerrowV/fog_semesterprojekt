package app.controllers;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.CarportSvg;
import io.javalin.http.Context;
import java.util.List;

public class OrderController {
    private CarportSvg carportSvg;

    public OrderController (Context ctx){

    }

    public void showOrder(){

    }

    public static void showOrders(Context ctx, ConnectionPool connectionPool) {

        int userId = ctx.sessionAttribute("user_id");

        try {
            List<Order> orders = OrderMapper.getOrderByUserId(userId, connectionPool);


            ctx.attribute("orders", orders);
            ctx.render("orders.html");


        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to load orders: " + e.getMessage());
            ctx.render("orders.html");
        }
    }

    public static void showAllOrdersWithDetails(Context ctx, ConnectionPool connectionPool) {
        try {
            String userEmail = ctx.sessionAttribute("userEmail");
            List<Order> allOrders = OrderMapper.getAllOrders(connectionPool);


            ctx.attribute("orders", allOrders);
            ctx.attribute("userEmail", userEmail);
            ctx.render("adminOrderList.html");

        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to load all orders: " + e.getMessage());
            ctx.render("index.html");
        }
    }
}