package app.controllers;

import app.entities.CarportSpec;
import app.entities.Material;
import app.entities.MaterialSpec;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.*;
import app.services.CarportSvg;
import io.javalin.http.Context;

import java.sql.Timestamp;
import java.util.*;

public class OrderController {
    private CarportSvg carportSvg;

    public OrderController(Context ctx) {

    }

    public static void showSVG(Context ctx, CarportSpec carportSpec, List<MaterialSpec> materialSpecs, List<Material> materials) {
        Locale.setDefault(new Locale("US"));
        CarportSvg carportSvg = new CarportSvg(carportSpec.getWidth(), carportSpec.getLength());
        carportSvg.addMaterials(materialSpecs, materials);

        ctx.attribute("svg", carportSvg.toString());
    }


    public static void showOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            Integer userId = ctx.sessionAttribute("user_id");

            if (userId == null) {
                ctx.attribute("message", "You need to log in to view your orders.");
                ctx.redirect("/login");
                return;
            }

            List<Order> orders = OrderMapper.getOrderByUserId(userId, connectionPool);
            if (orders == null) {
                orders = new ArrayList<>();
            }

            Map<Integer, Double> orderPrices = new HashMap<>();
            Map<Integer, Timestamp> paidDates = new HashMap<>();

            for (Order order : orders) {
                if ("Approved".equalsIgnoreCase(order.getOrderStatus()) || "Completed".equalsIgnoreCase(order.getOrderStatus())) {
                    double price = ReceiptMapper.getReceiptPriceByOrderId(order.getOrderId(), connectionPool);
                    orderPrices.put(order.getOrderId(), price);
                }

                if ("Completed".equalsIgnoreCase(order.getOrderStatus())) {
                    Timestamp paidDate = ReceiptMapper.getReceiptPaidDate(order.getOrderId(), connectionPool);
                    paidDates.put(order.getOrderId(), paidDate);
                }
            }

            ctx.attribute("orders", orders);
            ctx.attribute("orderPrices", orderPrices);
            ctx.attribute("paidDates", paidDates);
            ctx.render("order.html");

        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to load orders: " + e.getMessage());
            ctx.render("order.html");
        }
    }

    public static void showAllOrdersWithDetails(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> allOrders = OrderMapper.getAllOrders(connectionPool);
            List<Map<String, Object>> ordersWithPrices = new ArrayList<>();

            for (Order order : allOrders) {
                double price = ReceiptMapper.getReceiptPriceByOrderId(order.getOrderId(), connectionPool);
                Map<String, Object> orderWithPrice = new HashMap<>();
                orderWithPrice.put("order", order);
                orderWithPrice.put("price", price);
                ordersWithPrices.add(orderWithPrice);
            }

            ctx.attribute("ordersWithPrices", ordersWithPrices);
            ctx.render("admin-order-list.html");

        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to load all orders: " + e.getMessage());
            ctx.redirect("/admin");
        } catch (Exception e) {
            ctx.attribute("message", "An unexpected error occurred.");
            ctx.redirect("/admin");
        }
    }


    public static void updateOrderStatus(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("order_id"));
            String newStatus = ctx.formParam("new_status");

            OrderMapper.updateOrderStatus(orderId, newStatus, connectionPool);

            ctx.redirect("/admin/orders");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", "Failed to update order status: " + e.getMessage());
            ctx.render("admin-order-list.html");
        }
    }


    public static void updateOrderPrice(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("order_id"));
            double percentage = Double.parseDouble(ctx.formParam("percentage"));

            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            CarportSpec carportSpec = CarportMapper.getCarportSpecsById(order.getCarportId(), connectionPool);

            double basePrice = CarportController.calculatorForPrice(carportSpec.getLength(), carportSpec.getWidth(), carportSpec.isRoofType(), connectionPool);

            double finalPrice = CarportController.calculatePercentage(basePrice, percentage, connectionPool);

            OrderMapper.updateOrderPrice(orderId, finalPrice, connectionPool);

            ctx.attribute("message", "Price updated successfully!");
            ctx.redirect("/admin/orders");

        } catch (Exception e) {
            ctx.attribute("message", "Failed to update price: " + e.getMessage());
            ctx.redirect("/admin/orders");
        }
    }

    public static void updateOrderDetails(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("order_id"));
            String newStatus = ctx.formParam("new_status");
            double overheadPercentage = Double.parseDouble(ctx.formParam("percentage"));

            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            CarportSpec carportSpec = CarportMapper.getCarportSpecsById(order.getCarportId(), connectionPool);

            double basePrice = CarportController.calculatorForPrice(
                    carportSpec.getLength(), carportSpec.getWidth(), carportSpec.isRoofType(), connectionPool);
            double finalPrice = CarportController.calculatePercentage(basePrice, overheadPercentage, connectionPool);

            OrderMapper.updateOrderStatus(orderId, newStatus, connectionPool);
            OrderMapper.updateOrderPrice(orderId, finalPrice, connectionPool);

            if ("Approved".equalsIgnoreCase(newStatus)) {
                Boolean isAdmin = ctx.sessionAttribute("is_admin");

                if (isAdmin != null && !isAdmin) {
                    ctx.sessionAttribute("current_order_id", orderId);
                    ctx.redirect("/payment");
                    return;
                }
            }

            ctx.attribute("message", "Order updated successfully!");
            ctx.redirect("/admin/orders");

        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", "Failed to update order details: " + e.getMessage());
            ctx.redirect("/admin/orders");
        }
    }


    public static void showOrderDetails(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));

            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            CarportSpec carportSpec = CarportMapper.getCarportSpecsById(order.getCarportId(), connectionPool);
            List<MaterialSpec> materialSpecs = MaterialMapper.getMaterialSpecsByCarportId(order.getCarportId(), connectionPool);
            List<Material> materials = MaterialMapper.getAllMaterials(connectionPool);

            ctx.attribute("order", order);
            ctx.attribute("carportSpec", carportSpec);
            ctx.attribute("materialSpecs", materialSpecs);

            showSVG(ctx, carportSpec, materialSpecs, materials);

            ctx.render("order-details.html");

        } catch (NumberFormatException e) {
            ctx.attribute("message", "Invalid order ID format.");
            ctx.render("order-details.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Error retrieving order details: " + e.getMessage());
            ctx.render("order-details.html");
        }
    }

    public static String generateSVG(CarportSpec carportSpec, List<MaterialSpec> materialSpecs, List<Material> materials) {
        Locale.setDefault(new Locale("US"));
        CarportSvg carportSvg = new CarportSvg(carportSpec.getWidth(), carportSpec.getLength());
        carportSvg.addMaterials(materialSpecs, materials);
        return carportSvg.toString();
    }

    public static void rejectOrder(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("order_id"));

            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            if (!"Approved".equalsIgnoreCase(order.getOrderStatus())) {
                ctx.attribute("message", "Order is not in an 'Approved' state and cannot be rejected.");
                ctx.redirect("/orders");
                return;
            }

            OrderMapper.updateOrderStatus(orderId, "Rejected", connectionPool);

            ctx.redirect("/contact");


        } catch (NumberFormatException e) {
            ctx.attribute("message", "Invalid order ID.");
            ctx.redirect("/orders");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to reject the order: " + e.getMessage());
            ctx.redirect("/orders");
        }
    }

    public static void showAdminOrderDetails(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));


            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            CarportSpec carportSpec = CarportMapper.getCarportSpecsById(order.getCarportId(), connectionPool);
            List<MaterialSpec> materialSpecs = MaterialMapper.getMaterialSpecsByCarportId(order.getCarportId(), connectionPool);
            List<Material> materials = MaterialMapper.getAllMaterials(connectionPool);

            ctx.attribute("order", order);
            ctx.attribute("carportSpec", carportSpec);
            ctx.attribute("materialSpecs", materialSpecs);
            ctx.attribute("materials", materials);

            ctx.render("admin-order-details.html");
        } catch (NumberFormatException e) {
            ctx.attribute("message", "Invalid order ID format.");
            ctx.render("admin-order-list.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Error retrieving order details: " + e.getMessage());
            ctx.render("admin-order-list.html");
        }
    }
}