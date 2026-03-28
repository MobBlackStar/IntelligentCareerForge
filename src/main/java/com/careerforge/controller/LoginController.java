package com.careerforge.controller;

import com.careerforge.dao.UserDAO;
import com.careerforge.model.User;
import com.careerforge.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        IO.println("🛡️ SARAH UI: Attempting login for " + email);

        User loggedInUser = userDAO.authenticate(email, password);

        if (loggedInUser != null) {
            // FEDI-STANDARD: Lock the identity into the Global Matrix!
            UserSession.getInstance().login(loggedInUser);

            errorLabel.setText("✅ Welcome to the Forge, " + loggedInUser.getName() + "!");
            errorLabel.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold;");

            // SARAH: Add the code here tomorrow to switch the screen from Login.fxml to WarRoom.fxml!
            IO.println("✅ Login Success! Routing to War Room...");

        } else {
            errorLabel.setText("❌ Access Denied. The Phantoms reject you.");
            errorLabel.setStyle("-fx-text-fill: #ff0044; -fx-font-weight: bold;");
            IO.println("❌ Login Failed.");
        }
    }
}