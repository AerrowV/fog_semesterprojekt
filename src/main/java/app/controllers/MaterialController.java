package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class MaterialController {
    public static void saveCustomerSpecifications(Context ctx, ConnectionPool connectionPool) {

        //int carportSpecs = ctx.sessionAttribute("menu1");

        int length = Integer.parseInt(ctx.formParam("menu2"));
        int width = Integer.parseInt(ctx.formParam("menu1"));


        System.out.println(length + " " + width);

    }

}
