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
    public static void underFasciaBoardFront(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;

        if (width >= 240 && width <= 300) {
            boardId = 1;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 301 && width <= 360) {
            boardId = 2;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 361 && width <= 420) {
            boardId = 3;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 421 && width <= 480) {
            boardId = 4;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 481 && width <= 540) {
            boardId = 5;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 541 && width <= 600) {
            boardId = 6;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("understernbrædder til for & bag ende");
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

    public static void outerWaterBoardFrontend(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;

        if (width >= 240 && width <= 300) {
            boardId = 53;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i forende");
        } else if (width >= 301 && width <= 360) {
            boardId = 54;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i forende");
        } else if (width >= 361 && width <= 420) {
            boardId = 55;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i forende");
        } else if (width >= 421 && width <= 480) {
            boardId = 56;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i forende");
        } else if (width >= 481 && width <= 540) {
            boardId = 57;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i forende");
        } else if (width >= 541 && width <= 600) {
            boardId = 53;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("vandbrædt på stern i forende");
        }

    }

    public static void outerWaterBoardSides(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;

        if (length >= 240 && length <= 300) {
            boardId = 53;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i sider");
        } else if (length >= 301 && length <= 360) {
            boardId = 54;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i sider");
        } else if (length >= 361 && length <= 420) {
            boardId = 55;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i sider");
        } else if (length >= 421 && length <= 480) {
            boardId = 56;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i sider");
        } else if (length >= 481 && length <= 540) {
            boardId = 57;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("vandbrædt på stern i sider");
        } else if (length >= 541 && length <= 600) {
            boardId = 53;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("vandbrædt på stern i sider");
        } else if (length >= 601 && length <= 660) {
            boardId = 54;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("vandbrædt på stern i sider");
        } else if (length >= 661 && length <= 720) {
            boardId = 54;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("vandbrædt på stern i sider");
        } else if (length >= 721 && length <= 780) {
            boardId = 55;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("vandbrædt på stern i sider");
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



