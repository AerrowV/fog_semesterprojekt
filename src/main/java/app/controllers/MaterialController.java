package app.controllers;
import app.entities.Material;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import io.javalin.http.Context;
import java.util.List;

public class MaterialController {

    public static void showAllMaterials(Context ctx, ConnectionPool connectionPool) {

        try {
            List<Material> materials = MaterialMapper.getAllMaterials(connectionPool);
            ctx.attribute("materials", materials);
            ctx.render("materials.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to load materials: " + e.getMessage());
            ctx.render("error.html");
        }
    }

    public static void createMaterial(Context ctx, ConnectionPool connectionPool) {
        try {
            String description = ctx.formParam("description");
            int length = Integer.parseInt(ctx.formParam("length"));
            int amount = Integer.parseInt(ctx.formParam("amount"));
            String unit = ctx.formParam("unit");
            String function = ctx.formParam("function");
            int price = Integer.parseInt(ctx.formParam("price"));

            Material material = new Material(0, description, length, amount, unit, function, price);
            MaterialMapper.createMaterial(material, connectionPool);

            ctx.redirect("/materials");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to create material: " + e.getMessage());
            ctx.render("create-material.html");
        }
    }
}
