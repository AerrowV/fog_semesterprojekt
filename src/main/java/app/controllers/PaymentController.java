package app.controllers;

import app.entities.CarportSpec;
import app.entities.Material;
import app.entities.MaterialSpec;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.*;
import app.services.CarportSvg;
import app.services.MailService;
import io.javalin.http.Context;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

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

                sendReceiptEmail(orderId, email, connectionPool);

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

    private static void sendReceiptEmail(int orderId, String userEmail, ConnectionPool connectionPool) throws DatabaseException {
        try {
            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            CarportSpec carportSpec = CarportMapper.getCarportSpecsById(order.getCarportId(), connectionPool);
            List<MaterialSpec> materialSpecs = MaterialMapper.getMaterialSpecsByCarportId(order.getCarportId(), connectionPool);
            List<Material> materials = MaterialMapper.getAllMaterials(connectionPool);

            String svgContent = OrderController.generateSVG(carportSpec, materialSpecs, materials);

            StringBuilder materialListHtml = new StringBuilder("<ul>");
            for (MaterialSpec spec : materialSpecs) {
                Material material = materials.stream()
                        .filter(m -> m.getMaterialId() == spec.getMaterialId())
                        .findFirst()
                        .orElse(null);

                if (material != null) {
                    materialListHtml.append(String.format(
                            "<li>%s (Length: %d cm): %d %s</li>",
                            material.getDescription(),
                            material.getLength(),
                            spec.getMaterialOrderAmount(),
                            material.getUnit()
                    ));
                }
            }
            materialListHtml.append("</ul>");

            String emailContent = MailService.generateReceiptEmailContent(
                    order.getOrderId(),
                    materialListHtml.toString()
            );


            MailService.sendEmailWithAttachment(
                    userEmail,
                    "Johannes Fog A/S - Receipt for Order #" + order.getOrderId(),
                    "Please find the details of your order below.",
                    emailContent,
                    svgContent
            );

        } catch (Exception e) {
            throw new DatabaseException("Failed to send receipt email: " + e.getMessage());
        }
    }
}

