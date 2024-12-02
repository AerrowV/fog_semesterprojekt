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
    //Understernbrædder	til	for og bag enden
    public static Material underFasciaBoardFrontandBack(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId = 0;
        int amount=0;
        Material material = null;

        if (width >= 240 && width <= 300) {
            boardId = 1;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 301 && width <= 360) {
            boardId = 2;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 361 && width <= 420) {
            boardId = 3;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 421 && width <= 480) {
            boardId = 4;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 481 && width <= 540) {
            boardId = 5;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til for & bag ende");
        } else if (width >= 541 && width <= 600) {
            boardId = 6;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til for & bag ende");
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
        int boardId;

        if (length >= 240 && length <= 300) {
            boardId = 1;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 301 && length <= 360) {
            boardId = 2;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 361 && length <= 420) {
            boardId = 3;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 421 && length <= 480) {
            boardId = 4;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 481 && length <= 540) {
            boardId = 5;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 541 && length <= 600) {
            boardId = 6;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 601 && length <= 660) {
            boardId = 2;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 661 && length <= 720) {
            boardId = 2;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 721 && length <= 780) {
            boardId = 3;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("understernbrædder til siderne");
        }

    }

    //Oversternbrædder	til	forenden
    public static void overFasciaBoardFront(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId;

        if (width >= 240 && width <= 300) {
            boardId = 7;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("oversternbrædder til forenden");
        } else if (width >= 301 && width <= 360) {
            boardId = 8;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("oversternbrædder til forenden");
        } else if (width >= 361 && width <= 420) {
            boardId = 9;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("oversternbrædder til forenden");
        } else if (width >= 421 && width <= 480) {
            boardId = 10;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("oversternbrædder til forenden");
        } else if (width >= 481 && width <= 540) {
            boardId = 11;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("oversternbrædder til forenden");
        } else if (width >= 541 && width <= 600) {
            boardId = 12;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(1);
            board.setDescription("oversternbrædder til forenden");
        }
    }
    //Oversternbrædder	til	siderne
    public static void overFasciaBoardSides(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId;

        if (length >= 240 && length <= 300) {
            boardId = 7;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("oversternbrædder til siderne");
        } else if (length >= 301 && length <= 360) {
            boardId = 8;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("oversternbrædder til siderne");
        } else if (length >= 361 && length <= 420) {
            boardId = 9;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("oversternbrædder til siderne");
        } else if (length >= 421 && length <= 480) {
            boardId = 10;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("oversternbrædder til siderne");
        } else if (length >= 481 && length <= 540) {
            boardId = 11;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("oversternbrædder til siderne");
        } else if (length >= 541 && length <= 600) {
            boardId = 12;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("oversternbrædder til siderne");
        } else if (length >= 601 && length <= 660) {
            boardId = 8;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("oversternbrædder til siderne");
        } else if (length >= 661 && length <= 720) {
            boardId = 8;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("oversternbrædder til siderne");
        } else if (length >= 721 && length <= 780) {
            boardId = 9;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("oversternbrædder til siderne");
        }
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

        if (length > 400) {
            post.setAmount(6);
        } else {
            post.setAmount(4);
        }
    }
    //Bræt
    public static void sternBoard(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int boardId;


        if (length >= 240 && length <= 300) {
            boardId = 1;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 301 && length <= 360) {
            boardId = 2;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 361 && length <= 420) {
            boardId = 3;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 421 && length <= 480) {
            boardId = 4;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 481 && length <= 540) {
            boardId = 5;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 541 && length <= 600) {
            boardId = 6;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(2);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 601 && length <= 660) {
            boardId = 2;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 661 && length <= 720) {
            boardId = 3;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("understernbrædder til siderne");
        } else if (length >= 721 && length <= 780) {
            boardId = 3;
            Material board = MaterialMapper.getMaterialById(boardId, connectionPool);
            board.setAmount(4);
            board.setDescription("understernbrædder til siderne");
        }
    }
    //Spær
    public static void carportRafter(int length, int width, ConnectionPool connectionPool) throws DatabaseException {
        int amount = 0;
        if (length >= 240 && length <= 300) {
            amount = 5;
        } else if (length >= 301 && length <= 360) {
            amount = 6;
        } else if (length >= 361 && length <= 420) {
            amount = 7;
        } else if (length >= 421 && length <= 480) {
            amount = 8;
        } else if (length >= 481 && length <= 540) {
            amount = 9;
        } else if (length >= 541 && length <= 600) {
            amount = 10;
        } else if (length >= 601 && length <= 660) {
            amount = 11;
        } else if (length >= 661 && length <= 720) {
            amount = 13;
        } else if (length >= 721 && length <= 780) {
            amount = 14;
        }

        if (width >= 240 && width <= 300) {
            int rafterId = 34;
            Material rafter = MaterialMapper.getMaterialById(rafterId, connectionPool);
            rafter.setAmount(amount);
        } else if (width >= 301 && width <= 360) {
            int rafterId = 35;
            Material rafter = MaterialMapper.getMaterialById(rafterId, connectionPool);
            rafter.setAmount(amount);
        } else if (width >= 361 && width <= 420) {
            int rafterId = 36;
            Material rafter = MaterialMapper.getMaterialById(rafterId, connectionPool);
            rafter.setAmount(amount);
        } else if (width >= 421 && width <= 480) {
            int rafterId = 37;
            Material rafter = MaterialMapper.getMaterialById(rafterId, connectionPool);
            rafter.setAmount(amount);
        } else if (width >= 481 && width <= 540) {
            int rafterId = 38;
            Material rafter = MaterialMapper.getMaterialById(rafterId, connectionPool);
            rafter.setAmount(amount);
        } else if (width >= 541 && width <= 600) {
            int rafterId = 39;
            Material rafter = MaterialMapper.getMaterialById(rafterId, connectionPool);
            rafter.setAmount(amount);
        }



    }
}


