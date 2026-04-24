package com.careerforge;

import com.careerforge.core.SolanumCore;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 🛑 THE CORRECTION: Start at Login, NOT the Dashboard Shell
        // This ensures the user must authenticate first.
        Parent root = com.careerforge.core.SolanumCore.getInstance().observe("Login.fxml");

        Scene mainScene = new Scene(root);
        String css = getClass().getResource("/css/style.css").toExternalForm();
        mainScene.getStylesheets().add(css);

        primaryStage.setTitle("THE CAREER FORGE - Authentication Required");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        System.out.println("🔐 OMNI-CHIMERA: The Gate is locked. Awaiting Identity.");
    }

}