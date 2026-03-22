package com.careerforge.dao;

import com.careerforge.model.User;
import com.careerforge.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
// Fedi-Standard JDK 25 modern console output

/**
 * // FEDI: This class handles all MySQL operations for the User table.
 * // It implements our ICrud interface, meaning it MUST have create, readAll, update, and delete.
 */
public class UserDAO implements ICrud<User> {

    // Get the single connection instance from our Singleton
    private Connection connection = DatabaseConnection.getInstance().getConnection();

    /*
     * FEYNMAN COMMENT: What is a PreparedStatement and why do we use '?' ?
     * Imagine the database is an exclusive nightclub. A 'Statement' lets anyone walk in,
     * even hackers carrying SQL Injection bombs (like typing "1 OR 1=1" as a password).
     * A 'PreparedStatement' is a heavy-duty metal detector. The '?' are locked boxes.
     * We put the user's data inside the locked boxes. If the data is a bomb, it detonates
     * harmlessly inside the box, protecting the nightclub (database).
     */

    @Override
    public void create(User user) {
        // Fedi-Standard: JDK Text Blocks (""") make SQL highly readable. No more messy + strings!
        String query = """
            INSERT INTO users (name, email, password, target_job_title) 
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getTargetJobTitle());

            preparedStatement.executeUpdate();
            IO.println("✅ User inserted successfully: " + user.getName());

        } catch (SQLException e) {
            IO.println("❌ Error creating User: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<User> readAll() {
        ArrayList<User> userList = new ArrayList<>();
        String query = """
            SELECT * FROM users
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // FEYNMAN COMMENT: The ResultSet is a cursor reading the database table row by row.
            // while (resultSet.next()) moves the cursor down one row until there are no more rows.
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("target_job_title")
                );
                userList.add(user);
            }

        } catch (SQLException e) {
            IO.println("❌ Error reading Users: " + e.getMessage());
        }
        return userList;
    }

    @Override
    public void update(User user) {
        String query = """
            UPDATE users 
            SET name = ?, email = ?, password = ?, target_job_title = ? 
            WHERE id = ?
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getTargetJobTitle());
            preparedStatement.setInt(5, user.getId()); // The WHERE clause

            preparedStatement.executeUpdate();
            IO.println("✅ User updated successfully: " + user.getName());

        } catch (SQLException e) {
            IO.println("❌ Error updating User: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = """
            DELETE FROM users WHERE id = ?
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            IO.println("✅ User deleted successfully. ID: " + id);

        } catch (SQLException e) {
            IO.println("❌ Error deleting User: " + e.getMessage());
        }
    }

    /**
     * // SARAH: Use this method in the LoginController when the user clicks the "Connect" button!
     * // It checks the database and returns the User object if the email/password match.
     */
    public User authenticate(String email, String password) {
        String query = """
            SELECT * FROM users WHERE email = ? AND password = ?
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                IO.println("🔓 Authentication successful for: " + email);
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("target_job_title")
                );
            }

        } catch (SQLException e) {
            IO.println("❌ Error authenticating User: " + e.getMessage());
        }

        IO.println("🔒 Authentication failed for: " + email);
        return null; // Returns null if no match is found (wrong password/email)
    }
}