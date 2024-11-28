package app.controllers;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;

public class MailSystem {

    public static void main(String[] args) {
        String apiKey = System.getenv("MAILGUN_API_KEY");
        String domain = System.getenv("MAILGUN_DOMAIN");
        String sender = System.getenv("MAILGUN_SENDER_EMAIL");

        String euBaseUrl = "https://api.eu.mailgun.net/";

        MailgunMessagesApi mailgunMessagesApi = MailgunClient.config(euBaseUrl, apiKey).createApi(MailgunMessagesApi.class);

        Message message = Message.builder()
                .from(sender)
                .to("businessasimk@gmail.com")
                .subject("Hello from Mailgun")
                .text("This is a test email sent using the Mailgun Java SDK!")
                .build();

        MessageResponse response = mailgunMessagesApi.sendMessage(domain, message);
        System.out.println("Response ID: " + response.getId());
        System.out.println("Response Message: " + response.getMessage());

    }
}
