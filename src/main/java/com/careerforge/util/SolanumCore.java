package com.careerforge.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * // FEDI-STANDARD: THE SOLANUM CORE (State Manager)
 * // This is a Singleton that manages the entire application's views.
 */
public class SolanumCore {

    private static SolanumCore instance;
    private StackPane mainContent; // The empty space in MainLayout where pages appear

    private SolanumCore() {}

    public static SolanumCore getInstance() {
        if (instance == null) {
            instance = new SolanumCore();
        }
        return instance;
    }

    /**
     * // SARAH: This is the "Hook." Your MainLayoutController calls this
     * // to tell the Core where to project the rooms.
     */
    public void setMainContent(StackPane pane) {
        this.mainContent = pane;
    }

    /**
     * // THE QUANTUM MOON: This method loads any FXML file into the center area.
     * // Usage: SolanumCore.getInstance().observe("Arena.fxml");
     */
    public void observe(String fxmlFile) {
        if (mainContent == null) {
            System.out.println("❌ CORE ERROR: Main Content Area not set!");
            return;
        }

        try {
            // Load the new room blueprint
            Parent view = FXMLLoader.load(getClass().getResource("/view/" + fxmlFile));

            // Clear the old room and project the new one!
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);

            System.out.println("🌌 CORE: Quantum Moon materialized " + fxmlFile);
        } catch (IOException e) {
            System.out.println("❌ CORE CRASH: Could not find " + fxmlFile);
            e.printStackTrace();
        }
    }
}
