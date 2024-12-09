package app.services;

import app.entities.Order;
import app.entities.Receipt;
import app.entities.User;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MailService {

    private static final String API_KEY = System.getenv("MAILGUN_API_KEY");
    private static final String DOMAIN = System.getenv("MAILGUN_DOMAIN");
    private static final String SENDER_EMAIL = System.getenv("MAILGUN_SENDER_EMAIL");
    private static final String BASE_URL = "https://api.eu.mailgun.net/";

    public static MailgunMessagesApi getMailgunMessagesApi() {
        return MailgunClient.config(BASE_URL, API_KEY).createApi(MailgunMessagesApi.class);
    }

    public static void sendEmail(String toEmail, String subject, String body, String htmlContent) {

        Message message = Message.builder().from(SENDER_EMAIL).to(toEmail).subject(subject).text(body).html(htmlContent).build();

        MessageResponse response = getMailgunMessagesApi().sendMessage(DOMAIN, message);
    }

    public static void sendEmailWithAttachment(String toEmail, String subject, String body, String htmlContent, String svgContent) throws IOException {

        Message message = Message.builder().from(SENDER_EMAIL).to(toEmail).subject(subject).text(body).html(htmlContent).attachment(generateSvgFile(svgContent, "carport.svg")).build();

        MessageResponse response = getMailgunMessagesApi().sendMessage(DOMAIN, message);
    }


    public static String getAdminEmailTemplate(String userName, String adminMessage, String adminName, String adminTitle) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f9;
                            margin: 0;
                            padding: 0;
                            line-height: 1.6;
                        }
                        .container {
                            max-width: 600px;
                            margin: 20px auto;
                            background: #ffffff;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                            padding: 20px;
                        }
                        .header {
                            background-color: #1b3e5b;
                            color: #ffffff;
                            padding: 20px;
                            border-radius: 8px 8px 0 0;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .content p {
                            margin: 10px 0;
                            font-size: 16px;
                            color: #333333;
                        }
                        .content a {
                            color: #1b3e5b;
                            text-decoration: none;
                        }
                        .content a:hover {
                            text-decoration: underline;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 12px;
                            color: #777777;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Johannes Fog A/S</h1>
                        </div>
                        <div class="content">
                            <p>Dear %s,</p>
                            <p>Thank you for contacting us at Johannes Fog.</p>
                            <p>%s</p>
                            <p>If you have any further questions or need additional assistance, please feel free to reach out to us at <a href="mailto:support@fog.dk">support@fog.dk</a> or visit our website at <a href="https://www.fog.dk">www.fog.dk</a>.</p>
                            <p>We value your trust in Johannes Fog and look forward to serving you again in the future.</p>
                            <p>Best regards,</p>
                            <p><strong>%s</strong></p>
                            <p>%s</p>
                            <p>Johannes Fog A/S</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 Johannes Fog A/S. All rights reserved.</p>
                            <p>Visit us at <a href="https://www.johannesfog.dk">www.johannesfog.dk</a></p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(userName, adminMessage, adminName, adminTitle);
    }

    public static String generateReceiptEmailContent(Order order, User user, Receipt receipt, String materialListHtml) {
        return """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            body { font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; }
                            .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); padding: 20px; }
                            .header { background-color: #1b3e5b; color: #ffffff; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                            .content { padding: 20px; }
                            ul { list-style: none; padding: 0; }
                            li { margin: 5px 0; font-size: 14px; }
                            .footer { text-align: center; font-size: 12px; color: #777777; margin-top: 20px; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>Receipt for Your Order</h1>
                                <p>Order ID: %d</p>
                            </div>
                            <div class="content">
                                <p>Dear %s %s,</p>
                                <p>Thank you for your purchase at Johannes Fog A/S! Below, you will find the details of your order.</p>
                                <h2>Customer Details</h2>
                                <p><strong>Name:</strong> %s %s</p>
                                <p><strong>Email:</strong> %s</p>
                                <p><strong>Address:</strong> %s</p>
                                <h2>Order Details</h2>
                                <p><strong>Total Price:</strong> %.2f DKK</p>
                                <p><strong>Paid Date:</strong> %s</p>
                                <h2>Material List</h2>
                                <p>The following materials were used for your order:</p>
                                %s
                            </div>
                            <div class="footer">
                                <p>&copy; 2024 Johannes Fog A/S. All rights reserved.</p>
                                <p>Visit us at <a href="https://www.johannesfog.dk">www.johannesfog.dk</a> for more information.</p>
                                <p>For assistance, contact our support team at <a href="mailto:support@fog.dk">support@fog.dk</a>.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                """.formatted(order.getOrderId(),
                user.getFirstName(), user.getLastName(),
                user.getFirstName(), user.getLastName(),
                user.getEmail(),
                user.getFullAddress(),
                receipt.getPrice() * 1.0,
                receipt.getPaidDate().toString(),
                materialListHtml);
    }

    public static String generateWarehouseEmailContent(Order order, User user, Receipt receipt, String materialListHtml) {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); padding: 20px; }
                .header { background-color: #1b3e5b; color: #ffffff; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                .content { padding: 20px; }
                ul { list-style: none; padding: 0; }
                li { margin: 5px 0; font-size: 14px; }
                .footer { text-align: center; font-size: 12px; color: #777777; margin-top: 20px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Warehouse Order Notification</h1>
                    <p>Order ID: %d</p>
                </div>
                <div class="content">
                    <h2>Customer Details</h2>
                    <p><strong>Name:</strong> %s %s</p>
                    <p><strong>Email:</strong> %s</p>
                    <p><strong>Address:</strong> %s</p>
                    <h2>Order Details</h2>
                    <p><strong>Total Price:</strong> %.2f DKK</p>
                    <p><strong>Paid Date:</strong> %s</p>
                    <h2>Material List</h2>
                    <p>The following materials are required for this order:</p>
                    %s
                </div>
                <div class="footer">
                    <p>&copy; 2024 Johannes Fog A/S. All rights reserved.</p>
                    <p>Visit us at <a href="https://www.johannesfog.dk">www.johannesfog.dk</a> for more information.</p>
                </div>
            </div>
        </body>
        </html>
    """.formatted(order.getOrderId(),
                user.getFirstName(), user.getLastName(),
                user.getEmail(),
                user.getFullAddress(),
                receipt.getPrice() * 1.0,
                receipt.getPaidDate().toString(),
                materialListHtml);
    }

    public static File generateSvgFile(String svgContent, String fileName) throws IOException {

        File file = new File(fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(svgContent);
        }
        return file;
    }
}
