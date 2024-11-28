package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.http.Context;

public class CarportController {
    public static void saveCustomerSpecifications(Context ctx, ConnectionPool connectionPool) {

        //int carportSpecs = ctx.sessionAttribute("menu1");
        System.out.println("WOW");

        String width = ctx.formParam(ctx.formParam("width"));
        String length = ctx.formParam(ctx.formParam("length"));


        System.out.println(length + " " + width);

    }

}
