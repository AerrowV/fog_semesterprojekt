package app.controllers;

import app.services.MailService;
import io.javalin.http.Context;

public class MailController {

    public static void sendUserMail(Context ctx) {
        try {
            String name = ctx.formParam("name");
            String email = ctx.formParam("email");
            String subject = ctx.formParam("subject");
            String message = ctx.formParam("message");

            String adminEmail = System.getenv("MAILGUN_SENDER_EMAIL");

            MailService.sendEmail(adminEmail, "Message from " + name, ": " + subject, message + "\n\nFrom: " + email);

            ctx.attribute("message", "Your message has been sent!");
            ctx.render("contact.html");
        } catch (Exception e) {
            ctx.attribute("message", "Failed to send message: " + e.getMessage());
            ctx.render("contact.html");
        }
    }

    public static void sendAdminMail(Context ctx) {
        try {
            String userEmail = ctx.formParam("email");
            String subject = ctx.formParam("subject");
            String message = ctx.formParam("message");

            String htmlContent = MailService.getAdminEmailTemplate(
                    userEmail,
                    message,
                    "Anna Nielsen",
                    "Customer Support Specialist"
            );

            MailService.sendEmail(
                    userEmail,
                    subject,
                    message,
                    htmlContent
            );

            ctx.attribute("message", "Email sent successfully!");
            ctx.render("admin-emails.html");
        } catch (Exception e) {
            ctx.attribute("message", "Failed to send email: " + e.getMessage());
            ctx.render("admin-emails.html");
        }
    }
}

