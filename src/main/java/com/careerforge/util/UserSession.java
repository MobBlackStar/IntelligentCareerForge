package com.careerforge.util;

import com.careerforge.model.User;

/**
 * // FEDI: The Global Identity Matrix (Singleton).
 * // This holds the currently logged-in user so we don't have to pass the User object between 5 different JavaFX screens.
 */
public class UserSession {

    private static UserSession instance;
    private User activeUser;

    private UserSession() {
        // Private constructor to prevent mortals from making multiple sessions
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(User user) {
        this.activeUser = user;
        IO.println("🔐 SESSION LOCKED: Welcome, " + user.getName());
    }

    public void logout() {
        IO.println("🔓 SESSION CLEARED: Goodbye, " + (this.activeUser != null ? this.activeUser.getName() : "Unknown"));
        this.activeUser = null;
    }

    public User getActiveUser() {
        return this.activeUser;
    }

    public boolean isLoggedIn() {
        return this.activeUser != null;
    }
}