package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.http.Context;

public class CarportController {
    public static void saveCustomerSpecifications(Context ctx, ConnectionPool connectionPool) {

        try {
            int length = Integer.parseInt(ctx.formParam("length"));
            int width = Integer.parseInt(ctx.formParam("width"));
            System.out.println(length + " " + width);
        } catch (NumberFormatException | NullPointerException e) {
            System.err.println("Error parsing form parameters: " + e.getMessage());
        }
    }



}
