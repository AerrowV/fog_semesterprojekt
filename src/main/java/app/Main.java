package app;

import app.config.ThymeleafConfig;
import app.controllers.CarportController;
import app.controllers.MaterialController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

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

        app.get("/materials", ctx -> MaterialController.showAllMaterials(ctx, connectionPool));
        app.get("/materials/create", ctx -> MaterialController.showCreateMaterialForm(ctx));
        app.post("/materials/create", ctx -> MaterialController.createMaterial(ctx, connectionPool));
        app.get("/materials/update/", ctx -> MaterialController.showUpdateMaterialForm(ctx, connectionPool));
        app.post("/materials/update", ctx -> MaterialController.updateMaterial(ctx, connectionPool));
        app.post("/materials/delete", ctx -> MaterialController.deleteMaterial(ctx, connectionPool));

        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/chooseCarport", ctx -> ctx.render("chooseCarport.html"));
        app.post("/chooseCarport", ctx -> CarportController.saveCustomerSpecifications(ctx, connectionPool));
    }
}