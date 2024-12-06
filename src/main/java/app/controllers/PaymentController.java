package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.ReceiptMapper;
import app.persistence.UserMapper;
import io.javalin.http.Context;

import java.sql.Timestamp;

public class PaymentController {

    public static void saveUserData(Context ctx, ConnectionPool connectionPool) {
        try {
            String email = ctx.formParam("email");
            String firstName = ctx.formParam("first-name");
            String lastName = ctx.formParam("last-name");
            String address = ctx.formParam("street-name");
            String houseNumber = ctx.formParam("house-number");
            String zipString = ctx.formParam("zip");
            String orderIdString = ctx.formParam("orderId");

            int zip;
            int orderId;

            try {
                zip = Integer.parseInt(zipString);
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid ZIP code format.");
                return;
            }

            try {
                orderId = Integer.parseInt(orderIdString);
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid Order ID format.");
                return;
            }

            boolean zipCodeExists = UserMapper.checkZipCode(zip, connectionPool);
            boolean emailExists = UserMapper.checkEmail(email, connectionPool);

            if (emailExists && zipCodeExists) {
                UserMapper.saveUserDataToDB(email, firstName, lastName, address, houseNumber, zip, connectionPool);
                OrderMapper.updateOrderStatus(orderId, "Completed", connectionPool);

                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                ReceiptMapper.updatePaidDate(orderId, currentTimestamp, connectionPool);

                String htmlResponse = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="refresh" content="5;url=/">
                    <title>Payment Successful</title>
                </head>
                <body>
                    <h1>Payment Successful</h1>
                    <p>Your payment was successful. You will be redirected to the home page in 5 seconds.</p>
                    <p>If not, click <a href="/">here</a>.</p>
                </body>
                </html>
            """;
                ctx.html(htmlResponse);
            } else {
                ctx.status(400).result("Email or ZIP code not found. Please try again.");
            }
        } catch (DatabaseException e) {
            ctx.status(500).result(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("An unexpected error occurred.");
        }
    }


}

