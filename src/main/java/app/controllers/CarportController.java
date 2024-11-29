package app.controllers;

import app.entities.Material;
import app.exceptions.DatabaseException;
import app.persistence.CarportMapper;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import io.javalin.http.Context;

public class CarportController {

    public static void saveCustomerSpecifications(Context ctx, ConnectionPool connectionPool) {
        boolean hasRoof = false;
        try {
            int length = Integer.parseInt(ctx.formParam("length"));
            int width = Integer.parseInt(ctx.formParam("width"));

            CarportMapper.carportSpecs(length, width, hasRoof, connectionPool);


        } catch (NumberFormatException | NullPointerException | DatabaseException e) {
            System.err.println("Error parsing form parameters: " + e.getMessage());
        }

    }
    //Understernbrædder	til	forenden
    public static Material underFasciaBoardFront(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        int amount=0;
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

        if (boardId > 0) {
            material = MaterialMapper.getMaterialById(boardId, connectionPool);
            material.setAmount(1);
            material.setDescription("understernbrædder til for & bag ende");
        }
        if (material != null) {
            return material;
        } else {
            throw new DatabaseException("Width value is out of valid range.");
        }
    }

    //Understernbrædder	til	siderne
    public static void underFasciaBoardSides(int length, int width, ConnectionPool connectionPool) throws DatabaseException {

    }

    //Oversternbrædder	til	forenden
    public static void overFasciaBoardFront(int length, int width, ConnectionPool connectionPool) throws DatabaseException {

    }
    //Oversternbrædder	til	siderne
    public static void overFasciaBoardSides(int length, int width, ConnectionPool connectionPool) throws DatabaseException {

    }

    public static Material outerWaterBoardFrontend(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
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

        if (boardId > 0) {
            material = MaterialMapper.getMaterialById(boardId, connectionPool);
            material.setAmount(width >= 541 && width <= 600 ? 2 : 1); // Set amount conditionally
            material.setDescription("vandbrædt på stern i forende");
        }

        if (material != null) {
            return material;
        } else {
            throw new DatabaseException("Width value is out of valid range.");
        }
    }


    public static Material outerWaterBoardSides(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
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

        if (boardId > 0) {
            material = MaterialMapper.getMaterialById(boardId, connectionPool);
            material.setAmount(amount);
            material.setDescription("vandbrædt på stern i sider");
        }

        if (material != null) {
            return material;
        } else {
            throw new DatabaseException("Length value is out of valid range.");
        }
    }


    //Stolper til carport
    public static void carportPosts(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int postId = 45;
        Material post = MaterialMapper.getMaterialById(postId, connectionPool);

        if (length>400){
            post.setAmount(6);
        } else {
            post.setAmount(4);
        }

    }

    public static void carportRafter(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int rafterId = 45;

        switch ((width - 300) / 60) {


        }
    }


}



