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
            ctx.render("admin.html");
        }
    }

    public static void createMaterial(Context ctx, ConnectionPool connectionPool) {
        try {
            String description = ctx.formParam("description");
            int length = Integer.parseInt(ctx.formParam("length"));
            int amount = Integer.parseInt(ctx.formParam("amount"));
            String unit = ctx.formParam("unit");
            String function = ctx.formParam("function");

            String priceParam = ctx.formParam("price");
            if (priceParam == null || priceParam.isEmpty()) {
                throw new IllegalArgumentException("Price is required and cannot be empty.");
            }

            int price = Integer.parseInt(priceParam);
            if (price < 0) {
                throw new IllegalArgumentException("Price cannot be negative.");
            }

            Material material = new Material(description, length, amount, unit, function, price);
            MaterialMapper.createMaterial(material, connectionPool);

            ctx.redirect("/materials");
        } catch (NumberFormatException e) {
            ctx.attribute("message", "Invalid input: " + e.getMessage());
            ctx.render("create-material.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to create material: " + e.getMessage());
            ctx.render("create-material.html");
        } catch (IllegalArgumentException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("create-material.html");
        }
    }

    public static void showUpdateMaterialForm(Context ctx, ConnectionPool connectionPool) {
        try {
            int id = Integer.parseInt(ctx.queryParam("id"));
            Material material = MaterialMapper.getMaterialById(id, connectionPool);
            ctx.attribute("material", material);
            ctx.render("update-material.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to fetch material: " + e.getMessage());
            ctx.render("admin.html");
        }
    }

    public static void updateMaterial(Context ctx, ConnectionPool connectionPool) {
        try {
            int id = Integer.parseInt(ctx.formParam("id"));
            String description = ctx.formParam("description");
            int length = Integer.parseInt(ctx.formParam("length"));
            int amount = Integer.parseInt(ctx.formParam("amount"));
            String unit = ctx.formParam("unit");
            String function = ctx.formParam("function");
            int price = Integer.parseInt(ctx.formParam("price"));

            Material material = new Material(id, description, length, amount, unit, function, price);
            MaterialMapper.updateMaterial(material, connectionPool);

            ctx.redirect("/materials");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to update material: " + e.getMessage());
            ctx.render("admin.html");
        }
    }

    public static void deleteMaterial(Context ctx, ConnectionPool connectionPool) {
        try {
            int id = Integer.parseInt(ctx.formParam("id"));
            MaterialMapper.deleteMaterial(id, connectionPool);
            ctx.redirect("/materials");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to delete material: " + e.getMessage());
            ctx.render("admin.html");
        }
    }
}
