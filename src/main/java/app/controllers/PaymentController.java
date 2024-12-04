package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.http.Context;

public class PaymentController {

    public static void saveUserData(Context ctx, ConnectionPool connectionPool) {
        try {
            String email = ctx.formParam("email");
            String firstName = ctx.formParam("first-name");
            String lastName = ctx.formParam("last-name");
            String address = ctx.formParam("street-name");
            String houseNumber = ctx.formParam("house-number");
            String zipString = ctx.formParam("zip");


            int zip;
            try {
                zip = Integer.parseInt(zipString);
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid ZIP code format.");
                return;
            }

            boolean zipCodeExists = UserMapper.checkZipCode(zip, connectionPool);

            boolean emailExists = UserMapper.checkEmail(email, connectionPool);

            if (emailExists && zipCodeExists) {
                UserMapper.saveUserDataToDB(email, firstName, lastName, address, houseNumber, zip, connectionPool);
                ctx.status(200).result("Payment and billing data saved successfully.");
            } else {
                ctx.status(400).result("Email or Zipcode not found. Please try again.");
            }
        } catch (DatabaseException e) {
            ctx.status(500).result(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("An unexpected error occurred.");
        }
    }
}

