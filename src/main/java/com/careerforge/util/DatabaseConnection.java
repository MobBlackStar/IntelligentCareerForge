package com.careerforge.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 // Fedi-Standard JDK modern console output

/**
 * // FEDI: Singleton pattern for our Database Connection.
 * // SARAH: Don't instantiate this! Just call DatabaseConnection.getInstance().getConnection() in your controllers if needed.
 */
public class DatabaseConnection {

    // 1. The static instance of itself
    private static DatabaseConnection instance;
    private Connection connection;

    // Database credentials
    private final String URL = "jdbc:mysql://localhost:3306/career_forge_omni";
    private final String USER = "root";
    private final String PASS = ""; // Change if your Wamp/Xampp has a password!

    /*
     * FEYNMAN COMMENT: Why is the constructor PRIVATE?
     * Imagine the database is a highly secure VIP club. We only want ONE bouncer (the connection pipe).
     * If this was public, someone could type `new DatabaseConnection()` 100 times,
     * spawn 100 bouncers, and completely crash the club's memory.
     */
    private DatabaseConnection() {
        try {
            // THE WAKE-UP CALL: Force the JVM to explicitly load the Maven Translator
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(this.URL, this.USER, this.PASS);
            IO.println("🔥 Fedi-Standard: Omni-Database Connected Successfully!");

        } catch (ClassNotFoundException e) {
            // Java forced us to write this catch block!
            IO.println("❌ Maven Translator Asleep/Missing! " + e.getMessage());
        } catch (SQLException e) {
            // This catches wrong passwords or offline databases
            IO.println("❌ Database Connection Failed: " + e.getMessage());
        }
    }

    // 2. The global access point (Singleton Method)
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // 3. Getter for the actual java.sql.Connection
    public Connection getConnection() {
        return this.connection;
    }
}