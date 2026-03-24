package com.careerforge.controller;

import com.careerforge.dao.UserDAO;
import com.careerforge.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    // SARAH: These @FXML tags are the invisible wires that connect to your SceneBuilder UI!
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // Summoning Fedi's Database Engine
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        IO.println("🛡️ SARAH UI: Attempting login for " + email);

        // Asking Fedi's DAO if the user exists
        User loggedInUser = userDAO.authenticate(email, password);

        if (loggedInUser != null) {
            // SUCCESS! Neon Green
            errorLabel.setText("✅ Welcome to the Forge, " + loggedInUser.getName() + "!");
            errorLabel.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold;");
            IO.println("✅ Login Success! Routing to War Room...");
        } else {
            // FAILURE! Neon Red
            errorLabel.setText("❌ Access Denied. The Phantoms reject you.");
            errorLabel.setStyle("-fx-text-fill: #ff0044; -fx-font-weight: bold;");
            IO.println("❌ Login Failed.");
        }
    }
}
