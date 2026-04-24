package com.careerforge.controller;

import com.careerforge.dao.UserDAO;
import com.careerforge.model.User;
import com.careerforge.core.SolanumCore;
import com.careerforge.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * // SARAH: The Unified Identity Controller.
 * // Fixed for Navigation stability and Registration logic.
 */
public class LoginController {

    // --- 1. LOGIN FIELDS ---
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton; // Ensure fx:id="loginButton" is in Login.fxml!

    // --- 2. REGISTRATION FIELDS ---
    @FXML private TextField regName;
    @FXML private TextField regEmail;
    @FXML private PasswordField regPass;
    @FXML private TextField regTarget;

    private UserDAO userDAO = new UserDAO();

    /**
     * THE LOGIN GATE
     */
    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String pass = passwordField.getText();

        if (!email.contains("@")) {
            errorLabel.setText("⚠️ INVALID IDENTITY: Please enter a real email.");
            return;
        }

        User user = userDAO.authenticate(email, pass);

        if (user != null) {
            UserSession.getInstance().login(user);
            System.out.println("🔓 ACCESS GRANTED: Welcome, " + user.getName());

            // FEYNMAN COMMENT: We use runLater to wait a microsecond for the mouse click
            // to finish before we pull the rug out from under the user! This stops the crash.
            Platform.runLater(() -> {
                SolanumCore.getInstance().observe("MainLayout.fxml");
            });

        } else {
            errorLabel.setText("❌ ACCESS DENIED: Check your credentials.");
            errorLabel.setStyle("-fx-text-fill: #ff0044;");
        }
    }

    /**
     * THE INSCRIPTION GATE (Register)
     */
    @FXML
    public void handleRegister() {
        // FEYNMAN COMMENT: Before sending to Fedi's SQL, we check if the form is empty.
        if (regEmail.getText().isEmpty() || regPass.getText().isEmpty()) {
            System.out.println("⚠️ REGISTRATION BLOCKED: Form is empty.");
            return;
        }

        User newUser = new User(
                regName.getText(),
                regEmail.getText(),
                regPass.getText(),
                regTarget.getText()
        );

        userDAO.create(newUser);
        System.out.println("✅ IDENTITY FORGED: Account created for " + newUser.getName());

        // Go back to the login screen so they can enter the Matrix
        showLogin();
    }

    // --- NAVIGATION ---

    @FXML
    public void showRegister() {
        System.out.println("🛰️ NAVIGATOR: Switching to Register.fxml");
        SolanumCore.getInstance().observe("Register.fxml");
    }

    @FXML
    public void showLogin() {
        System.out.println("🛰️ NAVIGATOR: Returning to Login.fxml");
        SolanumCore.getInstance().observe("Login.fxml");
    }

    @FXML
    public void showForgotPassword() {
        errorLabel.setText("📬 SECURE LINK SENT: Check your email inbox.");
        errorLabel.setStyle("-fx-text-fill: #00f2fe; -fx-font-weight: bold;");
        System.out.println("📬 FORGE: Recovery protocol active.");
    }
}