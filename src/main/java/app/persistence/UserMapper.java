package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from user where user_email = ? and user_password = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    boolean isAdmin = rs.getBoolean("is_admin");
                    int id = rs.getInt("user_id");
                    return new User(id,email,password,isAdmin);
                } else {
                    throw new DatabaseException("Error in login. Try again");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into User (user_email, user_password) values (?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, password);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DatabaseException("Error creating user");
                }
            }
        } catch (SQLException e) {
            String msg = "Error creating user";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "E-mail already exists";
            }
            throw new DatabaseException(msg);
        }
    }
}
