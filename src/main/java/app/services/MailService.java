package app.services;

import app.entities.Order;
import app.entities.Receipt;
import app.entities.User;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

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

        getMailgunMessagesApi().sendMessage(DOMAIN, message);
    }

    public static void sendEmailWithAttachment(String toEmail, String subject, String body, String htmlContent, String svgContent) throws IOException {

        Message message = Message.builder().from(SENDER_EMAIL).to(toEmail).subject(subject).text(body).html(htmlContent).attachment(generateSvgFile(svgContent, "carport.svg")).build();

        getMailgunMessagesApi().sendMessage(DOMAIN, message);
    }


    public static String getAdminEmailTemplate(String userName, String adminMessage, String adminName, String adminTitle) {
        return """
                <!DOCTYPE html>
                <html lang="da">
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
                            <p>Kære %s,</p>
                            <p>Tak fordi du kontaktede os hos Johannes Fog.</p>
                            <p>%s</p>
                            <p>Hvis du har yderligere spørgsmål eller brug for hjælp, er du velkommen til at kontakte os på <a href="mailto:support@mail.yumiya.dk">support@mail.yumiya.dk</a> eller besøge vores hjemmeside på <a href="https://www.johannesfog.dk">www.johannesfog.dk</a>.</p>
                            <p>Vi værdsætter din tillid til Johannes Fog og ser frem til at betjene dig igen i fremtiden.</p>
                            <p>Med venlig hilsen,</p>
                            <p><strong>%s</strong></p>
                            <p>%s</p>
                            <p>Johannes Fog A/S</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 Johannes Fog A/S. Alle rettigheder forbeholdes.</p>
                            <p>Besøg os på <a href="https://www.johannesfog.dk">www.johannesfog.dk</a></p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(userName, adminMessage, adminName, adminTitle);
    }

    public static String generateReceiptEmailContent(Order order, User user, Receipt receipt, String materialListHtml) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedPaidDate = dateFormatter.format((receipt.getPaidDate()));
        return """
                <!DOCTYPE html>
                <html lang="da">
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
                            <h1>Kvittering for din ordre</h1>
                            <p>Ordre ID: %d</p>
                        </div>
                        <div class="content">
                            <p>Kære %s %s,</p>
                            <p>Tak for dit køb hos Johannes Fog A/S! Herunder finder du detaljer om din ordre.</p>
                            <h2>Kundedetaljer</h2>
                            <p><strong>Navn:</strong> %s %s</p>
                            <p><strong>Email:</strong> %s</p>
                            <p><strong>Adresse:</strong> %s</p>
                            <h2>Ordredetaljer</h2>
                            <p><strong>Total Pris:</strong> %.2f DKK</p>
                            <p><strong>Betalingsdato:</strong> %s</p>
                            <h2>Materialeliste</h2>
                            <p>Følgende materialer blev brugt til din ordre:</p>
                            %s
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 Johannes Fog A/S. Alle rettigheder forbeholdes.</p>
                            <p>Besøg os på <a href="https://www.johannesfog.dk">www.johannesfog.dk</a> for mere information.</p>
                            <p>For hjælp, kontakt vores supportteam på <a href="mailto:support@mail.yumiya.dk">support@mail.yumiya.dk</a>.</p>
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
                formattedPaidDate,
                materialListHtml);
    }

    public static String generateWarehouseEmailContent(Order order, User user, Receipt receipt, String materialListHtml) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedPaidDate = dateFormatter.format((receipt.getPaidDate()));
        return """
                <!DOCTYPE html>
                <html lang="da">
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
                            <h1>Notifikation fra lager</h1>
                            <p>Ordre ID: %d</p>
                        </div>
                        <div class="content">
                            <h2>Kundedetaljer</h2>
                            <p><strong>Navn:</strong> %s %s</p>
                            <p><strong>Email:</strong> %s</p>
                            <p><strong>Adresse:</strong> %s</p>
                            <h2>Ordredetaljer</h2>
                            <p><strong>Total Pris:</strong> %.2f DKK</p>
                            <p><strong>Betalingsdato:</strong> %s</p>
                            <h2>Materialeliste</h2>
                            <p>Følgende materialer er nødvendige til denne ordre:</p>
                            %s
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 Johannes Fog A/S. Alle rettigheder forbeholdes.</p>
                            <p>Besøg os på <a href="https://www.johannesfog.dk">www.johannesfog.dk</a> for mere information.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(order.getOrderId(),
                user.getFirstName(), user.getLastName(),
                user.getEmail(),
                user.getFullAddress(),
                receipt.getPrice() * 1.0,
                formattedPaidDate,
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
