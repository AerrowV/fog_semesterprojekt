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

            CarportMapper.saveSpecs(length, width, hasRoof, connectionPool);


        } catch (NumberFormatException | NullPointerException | DatabaseException e) {
            System.err.println("Error parsing form parameters: " + e.getMessage());
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



