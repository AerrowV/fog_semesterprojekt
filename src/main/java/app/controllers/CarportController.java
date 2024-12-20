package app.controllers;

import app.entities.Material;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.*;
import io.javalin.http.Context;

import java.util.ArrayList;

public class CarportController {

    public static void saveCustomerSpecifications(Context ctx, ConnectionPool connectionPool) {
        try {
            Integer userId = ctx.sessionAttribute("user_id");
            if (userId == null) {
                ctx.attribute("message", "You must be logged in to create a carport specification.");
                ctx.redirect("/login");
                return;
            }

            int length = Integer.parseInt(ctx.formParam("length"));
            int width = Integer.parseInt(ctx.formParam("width"));
            boolean hasRoof = Boolean.parseBoolean(ctx.formParam("roof"));

            int carportId = CarportMapper.saveCarportSpecs(length, width, hasRoof, connectionPool);
            ArrayList<Material> stykListe = carportStykListe(length, width, carportId, connectionPool);
            saveStykliste(connectionPool, stykListe, carportId);

            Order newOrder = new Order(0, null, "In Review", userId, carportId);
            int orderId = OrderMapper.createOrder(newOrder, connectionPool);

            if (orderId <= 0) {
                throw new DatabaseException("Order creation returned invalid ID.");
            }
            double price = calculatorForPrice(length, width, hasRoof, connectionPool);
            ReceiptMapper.saveReceiptPrice(orderId, price, connectionPool);

            ctx.attribute("message", "Carport specifications and order created successfully.");
            ctx.redirect("/orders");
        } catch (NumberFormatException e) {
            ctx.attribute("message", "Invalid input. Please check your values.");
            ctx.render("choose-carport.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to save carport specifications: " + e.getMessage());
            ctx.render("choose-carport.html");
        } catch (Exception e) {
            ctx.attribute("message", "An unexpected error occurred.");
            ctx.render("choose-carport.html");
        }
    }


    public static Material underFasciaBoardFrontandBack(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        int amount = 0;
        Material material = null;

        if (width >= 240 && width <= 300) {
            boardId = 1;
        } else if (width >= 301 && width <= 360) {
            boardId = 2;
        } else if (width >= 361 && width <= 420) {
            boardId = 3;
        } else if (width >= 421 && width <= 480) {
            boardId = 4;
        } else if (width >= 481 && width <= 540) {
            boardId = 5;
        } else if (width >= 541 && width <= 600) {
            boardId = 6;
        }

        material = MaterialMapper.getMaterialById(boardId, connectionPool);
        material.setAmount(2);
        material.setDescription("understernbrædder til for & bag ende");
        material.setPrice(material.getAmount() * material.getPrice());

        return material;
    }

    public static Material underFasciaBoardSides(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        int amount = 0;
        Material material = null;

        if (length >= 240 && length <= 300) {
            boardId = 1;
            amount = 2;
        } else if (length >= 301 && length <= 360) {
            boardId = 2;
            amount = 2;
        } else if (length >= 361 && length <= 420) {
            boardId = 3;
            amount = 2;
        } else if (length >= 421 && length <= 480) {
            boardId = 4;
            amount = 2;
        } else if (length >= 481 && length <= 540) {
            boardId = 5;
            amount = 2;
        } else if (length >= 541 && length <= 600) {
            boardId = 6;
            amount = 2;
        } else if (length >= 601 && length <= 660) {
            boardId = 2;
            amount = 4;
        } else if (length >= 661 && length <= 720) {
            boardId = 2;
            amount = 4;
        } else if (length >= 721 && length <= 780) {
            boardId = 3;
            amount = 4;
        }

        material = MaterialMapper.getMaterialById(boardId, connectionPool);
        material.setAmount(amount);
        material.setDescription("understernbrædder til siderne");
        material.setPrice(material.getAmount() * material.getPrice());

        return material;
    }

    public static Material overFasciaBoardFront(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        int amount = 0;
        Material material = null;

        if (width >= 240 && width <= 300) {
            boardId = 7;
            amount = 1;
        } else if (width >= 301 && width <= 360) {
            boardId = 8;
            amount = 1;
        } else if (width >= 361 && width <= 420) {
            boardId = 9;
            amount = 1;
        } else if (width >= 421 && width <= 480) {
            boardId = 10;
            amount = 1;
        } else if (width >= 481 && width <= 540) {
            boardId = 11;
            amount = 1;
        } else if (width >= 541 && width <= 600) {
            boardId = 12;
            amount = 1;
        }

        material = MaterialMapper.getMaterialById(boardId, connectionPool);
        material.setAmount(amount);
        material.setDescription("oversternbrædder til forenden");
        material.setPrice(material.getAmount() * material.getPrice());

        return material;
    }

    public static Material overFasciaBoardSides(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        int amount = 0;
        Material material = null;

        if (length >= 240 && length <= 300) {
            boardId = 7;
            amount = 2;
        } else if (length >= 301 && length <= 360) {
            boardId = 8;
            amount = 2;
        } else if (length >= 361 && length <= 420) {
            boardId = 9;
            amount = 2;
        } else if (length >= 421 && length <= 480) {
            boardId = 10;
            amount = 2;
        } else if (length >= 481 && length <= 540) {
            boardId = 11;
            amount = 2;
        } else if (length >= 541 && length <= 600) {
            boardId = 12;
            amount = 2;
        } else if (length >= 601 && length <= 660) {
            boardId = 8;
            amount = 4;
        } else if (length >= 661 && length <= 720) {
            boardId = 8;
            amount = 4;
        } else if (length >= 721 && length <= 780) {
            boardId = 9;
            amount = 4;
        }

        material = MaterialMapper.getMaterialById(boardId, connectionPool);
        material.setAmount(amount);
        material.setDescription("oversternbrædder til siderne");
        material.setPrice(material.getAmount() * material.getPrice());

        return material;
    }

    public static Material outerWaterBoardFrontend(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        Material material = null;

        if (width >= 240 && width <= 300) {
            boardId = 53;
        } else if (width >= 301 && width <= 360) {
            boardId = 54;
        } else if (width >= 361 && width <= 420) {
            boardId = 55;
        } else if (width >= 421 && width <= 480) {
            boardId = 56;
        } else if (width >= 481 && width <= 540) {
            boardId = 57;
        } else if (width >= 541 && width <= 600) {
            boardId = 53;
        }

        material = MaterialMapper.getMaterialById(boardId, connectionPool);
        material.setAmount(width >= 541 && width <= 600 ? 2 : 1);
        material.setDescription("vandbrædt på stern i forende");
        material.setPrice(material.getAmount() * material.getPrice());

        return material;
    }


    public static Material outerWaterBoardSides(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        int amount = 1;
        Material material = null;

        if (length >= 240 && length <= 300) {
            boardId = 53;
        } else if (length >= 301 && length <= 360) {
            boardId = 54;
        } else if (length >= 361 && length <= 420) {
            boardId = 55;
        } else if (length >= 421 && length <= 480) {
            boardId = 56;
        } else if (length >= 481 && length <= 540) {
            boardId = 57;
        } else if (length >= 541 && length <= 600) {
            boardId = 53;
            amount = 2;
        } else if (length >= 601 && length <= 660) {
            boardId = 54;
            amount = 2;
        } else if (length >= 661 && length <= 720) {
            boardId = 54;
            amount = 2;
        } else if (length >= 721 && length <= 780) {
            boardId = 55;
            amount = 2;
        }

        material = MaterialMapper.getMaterialById(boardId, connectionPool);
        material.setAmount(amount);
        material.setDescription("vandbrædt på stern i sider");
        material.setPrice(material.getAmount() * material.getPrice());

        return material;
    }

    public static Material carportPosts(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int postId = 45;
        Material post = MaterialMapper.getMaterialById(postId, connectionPool);

        if (length > 400) {
            post.setAmount(6);
        } else {
            post.setAmount(4);
        }

        return post;
    }

    public static Material sternBoard(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        int amount = 2;
        Material material = null;


        if (length >= 240 && length <= 300) {
            boardId = 1;
        } else if (length >= 301 && length <= 360) {
            boardId = 2;
        } else if (length >= 361 && length <= 420) {
            boardId = 3;
        } else if (length >= 421 && length <= 480) {
            boardId = 4;
        } else if (length >= 481 && length <= 540) {
            boardId = 5;
        } else if (length >= 541 && length <= 600) {
            boardId = 6;
        } else if (length >= 601 && length <= 660) {
            boardId = 2;
            amount = 4;
        } else if (length >= 661 && length <= 720) {
            boardId = 3;
            amount = 4;
        } else if (length >= 721 && length <= 780) {
            boardId = 3;
            amount = 4;
        }

        material = MaterialMapper.getMaterialById(boardId, connectionPool);
        material.setAmount(amount);
        material.setDescription("understernbrædder til siderne");
        material.setPrice(material.getAmount() * material.getPrice());

        return material;
    }

    public static Material carportRafter(double length, double width, ConnectionPool connectionPool) throws DatabaseException {
        int amount = 0;
        int rafterId = 0;
        if (length >= 240 && length <= 300) {
            amount = 6;
        } else if (length >= 301 && length <= 360) {
            amount = 7;
        } else if (length >= 361 && length <= 420) {
            amount = 8;
        } else if (length >= 421 && length <= 480) {
            amount = 9;
        } else if (length >= 481 && length <= 540) {
            amount = 10;
        } else if (length >= 541 && length <= 600) {
            amount = 11;
        } else if (length >= 601 && length <= 660) {
            amount = 12;
        } else if (length >= 661 && length <= 720) {
            amount = 14;
        } else if (length >= 721 && length <= 780) {
            amount = 15;
        }

        if (width >= 240 && width <= 300) {
            rafterId = 34;
        } else if (width >= 301 && width <= 360) {
            rafterId = 35;
        } else if (width >= 361 && width <= 420) {
            rafterId = 36;
        } else if (width >= 421 && width <= 480) {
            rafterId = 37;
        } else if (width >= 481 && width <= 540) {
            rafterId = 38;
        } else if (width >= 541 && width <= 600) {
            rafterId = 39;
        }

        Material rafter = MaterialMapper.getMaterialById(rafterId, connectionPool);
        rafter.setAmount(amount);
        rafter.setPrice(rafter.getAmount() * rafter.getPrice());

        return rafter;
    }

    public static ArrayList<Material> carportStykListe(double length, double width, int carportId, ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<Material> stykliste = new ArrayList<>();
        stykliste.add(outerWaterBoardFrontend(length, width, connectionPool));
        stykliste.add(outerWaterBoardSides(length, width, connectionPool));
        stykliste.add(carportRafter(length, width, connectionPool));
        stykliste.add(sternBoard(length, width, connectionPool));
        stykliste.add(carportPosts(length, width, connectionPool));
        stykliste.add(overFasciaBoardFront(length, width, connectionPool));
        stykliste.add(overFasciaBoardSides(length, width, connectionPool));
        stykliste.add(underFasciaBoardFrontandBack(length, width, connectionPool));
        stykliste.add(underFasciaBoardSides(length, width, connectionPool));

        return stykliste;
    }

    public static void saveStykliste(ConnectionPool connectionPool, ArrayList<Material> stykliste, int carportId) throws DatabaseException {
        for (Material material : stykliste) {
            CarportMapper.saveMaterialSpec(carportId, material.getMaterialId(), material.getAmount(), connectionPool);
            MaterialMapper.updateMaterialAmount(material.getMaterialId(), material.getAmount(), connectionPool);
        }
    }

    public static double calculatorForPrice(double length, double width, boolean hasRoof, ConnectionPool connectionPool) throws DatabaseException {

        int carportId = CarportMapper.getCarportId(length, width, hasRoof, connectionPool);

        ArrayList<Material> stykliste = carportStykListe(length, width, carportId, connectionPool);

        double totalPrice = 0.0;
        for (Material material : stykliste) {
            totalPrice += material.getPrice();
        }

        return totalPrice * 1.4;
    }

    public static double calculatePercentage(double totalPrice, double percentage, ConnectionPool connectionPool) throws DatabaseException {
        return totalPrice - ((percentage / 100) * totalPrice);

    }
}


