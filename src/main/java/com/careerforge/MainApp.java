package com.careerforge;

import com.careerforge.core.SolanumCore;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 🛑 FEDI-STANDARD: We ask the Solanum Core to materialize the MASTER SHELL!
        Parent root = SolanumCore.getInstance().observe("DashboardShell.fxml");

        Scene mainScene = new Scene(root);

        // The Global Theme Engine
        String css = getClass().getResource("/css/style.css").toExternalForm();
        mainScene.getStylesheets().add(css);

        primaryStage.setTitle("THE CAREER FORGE - Omni-Chimera");
        primaryStage.setScene(mainScene);

        // Make the window big enough for the Shell!
        primaryStage.setWidth(1366);
        primaryStage.setHeight(768);

        primaryStage.show();

        IO.println("✅ OMNI-CHIMERA ONLINE. The Living Shell is breathing.");
    }
}