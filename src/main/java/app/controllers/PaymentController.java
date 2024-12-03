package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.PaymentMapper;
import app.persistence.UserMapper;
import io.javalin.http.Context;

public class PaymentController {

    public static void saveUserData(Context ctx, ConnectionPool connectionPool) {

        try {
        String email = ctx.queryParam("email");
        String firstName = ctx.formParam("first-name");
        String lastName = ctx.formParam("last-name");
        String address = ctx.formParam("address");
        int zipCode = Integer.parseInt(ctx.formParam("zip"));

        boolean emailExists = UserMapper.checkEmail(email, connectionPool);
        if(emailExists){

            PaymentMapper.saveUserDataToDB(firstName, lastName, address, zipCode, connectionPool);

            } else {
            ctx.status(400).result("Email not found. Please try again.");
            }
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("payment.html");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("An unexpected error occurred.");
        }

    }

}
