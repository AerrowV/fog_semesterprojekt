package app;

import app.config.ThymeleafConfig;
import app.controllers.*;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import static app.controllers.UserController.renderHomePage;

public class Main {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "fog";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            config.staticFiles.add("/templates");
        }).start(7070);

        app.get("/", ctx -> renderHomePage(ctx));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> UserController.login(ctx, connectionPool));
        app.get("/register", ctx -> ctx.render("register.html"));
        app.post("/register", ctx -> UserController.createUser(ctx, connectionPool));
        app.post("/logout", ctx -> UserController.logout(ctx, connectionPool));
        app.get("/admin", ctx -> ctx.render("admin.html"));
        app.get("/materials", ctx -> MaterialController.showAllMaterials(ctx, connectionPool));
        app.get("/materials/create", ctx -> ctx.render("create-material.html"));
        app.post("/materials/create", ctx -> MaterialController.createMaterial(ctx, connectionPool));
        app.get("/materials/update/", ctx -> MaterialController.showUpdateMaterialForm(ctx, connectionPool));
        app.post("/materials/update", ctx -> MaterialController.updateMaterial(ctx, connectionPool));
        app.post("/materials/delete", ctx -> MaterialController.deleteMaterial(ctx, connectionPool));
        app.get("/chooseCarport", ctx -> ctx.render("choose-carport.html"));
        app.post("/chooseCarport", ctx -> CarportController.saveCustomerSpecifications(ctx, connectionPool));
        app.get("/orders/details/{id}", ctx -> OrderController.showOrderDetails(ctx, connectionPool));
        app.get("/payment", ctx -> ctx.render("payment.html"));
        app.post("/payment", ctx -> PaymentController.saveUserData(ctx, connectionPool));
        app.get("/orders", ctx -> OrderController.showOrders(ctx, connectionPool));
        app.get("/admin/orders", ctx -> OrderController.showAllOrdersWithDetails(ctx, connectionPool));
        app.post("/update-order-status", ctx -> OrderController.updateOrderStatus(ctx, connectionPool));
        app.post("/update-order-price", ctx -> OrderController.updateOrderPrice(ctx, connectionPool));
        app.post("/update-order-details", ctx -> OrderController.updateOrderDetails(ctx, connectionPool));
        app.post("/payment/complete", ctx -> PaymentController.saveUserData(ctx, connectionPool));
        app.get("/contact", ctx -> ctx.render("contact.html"));
        app.post("/contact", ctx -> MailController.sendUserMail(ctx));
        app.get("/admin/emails", ctx -> ctx.render("admin-emails.html"));
        app.post("/admin/emails", ctx -> MailController.sendAdminMail(ctx));
        app.post("/orders/reject", ctx -> OrderController.rejectOrder(ctx, connectionPool));
        app.get("/admin/orders/details/{id}", ctx -> OrderController.showAdminOrderDetails(ctx, connectionPool));
    }
}