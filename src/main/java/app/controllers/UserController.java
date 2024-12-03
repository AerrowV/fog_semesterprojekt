package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.http.Context;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {


    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.sessionAttribute("isLoggedIn", true);

            if (user.getIsAdmin()) {
                ctx.redirect("/admin");
            } else {
                ctx.sessionAttribute("user_id", user.getUserId());
                ctx.redirect("/");
            }
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }

    public static void createUser(Context ctx, ConnectionPool connectionPool) {

        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (password1.equals(password2)) {
            try {
                UserMapper.createUser(email, password1, connectionPool);
                ctx.attribute("message", "Account created");
                ctx.render("login.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("register.html");
            }
        } else {
            ctx.attribute("message", "Passwords do not match");
            ctx.render("register.html");
        }

    }

    public static boolean validateUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT user_password FROM \"user\" WHERE user_email = ?";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("user_password");
                        return password.equals(storedPassword);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error validating user");
        }
        return false;
    }

    public static void logout(Context ctx, ConnectionPool connectionPool) {
        ctx.sessionAttribute("currentUser", null);
        ctx.sessionAttribute("isLoggedIn", false);
        ctx.sessionAttribute("user_id", null);

        ctx.redirect("/");
    }

    public static void renderHomePage(Context ctx) {
        Boolean isLoggedIn = ctx.sessionAttribute("isLoggedIn");
        if (isLoggedIn == null) isLoggedIn = false;
        ctx.attribute("isLoggedIn", isLoggedIn);
        ctx.render("index.html");
    }

}
