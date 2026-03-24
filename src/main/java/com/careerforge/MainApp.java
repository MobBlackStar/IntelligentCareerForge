package com.careerforge;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Sarah: Ensure the "/" is before "view"!
        Parent root = FXMLLoader.load(getClass().getResource("/view/WarRoom.fxml"));

        primaryStage.setTitle("THE CAREER FORGE - Command Center");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        System.out.println("✅ WAR ROOM STABILIZED. The Arena is open.");
    }

    public static void main(String[] args) {
        System.out.println("=== 🚀 INITIATING OMNI-CHIMERA UI ===");
        launch(args);
    }
}