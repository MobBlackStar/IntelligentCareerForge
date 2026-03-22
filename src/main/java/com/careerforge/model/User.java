package com.careerforge.model;

/**
 * // FEDI: Sarah, this is our core User entity.
 * // Notice the Fedi-Standard strict encapsulation: all fields are private.
 */
public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private String targetJobTitle;

    /*
     * FEYNMAN COMMENT: Why two constructors?
     * Imagine making a completely new user on the Login screen. The database hasn't given them an ID yet,
     * so we use the constructor WITHOUT the 'id'.
     * But when we fetch an existing user FROM the database, we use the constructor WITH the 'id'.
     */

    // 1. Constructor for creating a NEW User
    public User(String name, String email, String password, String targetJobTitle) {
        // Fedi-Standard JDK 25: We could put validation logic here before super() if we inherited a class.
        this.name = name;
        this.email = email;
        this.password = password;
        this.targetJobTitle = targetJobTitle;
    }

    // 2. Constructor for fetching an EXISTING User from MySQL
    public User(int id, String name, String email, String password, String targetJobTitle) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.targetJobTitle = targetJobTitle;
    }

    // ================= GETTERS & SETTERS =================
    // FEYNMAN COMMENT: The 'this' keyword is strictly used to tell Java:
    // "Modify the attribute of THIS specific object, not the temporary parameter."

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    public String getTargetJobTitle() { return this.targetJobTitle; }
    public void setTargetJobTitle(String targetJobTitle) { this.targetJobTitle = targetJobTitle; }

    // ================= Fedi-Standard Display =================
    @Override
    public String toString() {
        return "User[" + this.name + " | Target: " + this.targetJobTitle + "]";
    }
}