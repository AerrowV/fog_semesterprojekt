package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.http.Context;

public class UserController {


    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        try {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);

            if (user.getIsAdmin()) {
                ctx.redirect("/admin");
            } else {
                ctx.sessionAttribute("currentUser", user);
                ctx.sessionAttribute("user_id", user.getUserId());
                ctx.redirect("/shopping");
            }
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }
    }

    public static void createUser(Context ctx, ConnectionPool connectionPool) {

        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (password1.equals(password2)) {
            try {
                UserMapper.createUser(email, password1, connectionPool);
                ctx.attribute("message", "You have successfully created a new customer");
                ctx.render("index.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("register.html");
            }
        } else {
            ctx.attribute("message", "Passwords do not match");
            ctx.render("register.html");
        }
    }
}
