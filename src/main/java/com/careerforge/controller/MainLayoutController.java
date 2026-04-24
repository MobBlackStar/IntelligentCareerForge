package com.careerforge.controller;

import com.careerforge.core.SolanumCore;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;


/**
 * // FEDI & SARAH: The Master Router for the Living Aquarium.
 * // It asks Solanum Core for the blueprint, and injects it into the center space.
 */
public class MainLayoutController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        IO.println("🌌 MAIN LAYOUT: Solanum Shell Initializing...");

        // Notice we do NOT use setMainContent() anymore! We keep MVC pure.
        showWarRoom(); // Load default page
    }

    @FXML public void showWarRoom() { loadChamber("WarRoom.fxml"); }
    @FXML public void showPhantomSandbox() { loadChamber("PhantomSandbox.fxml"); }
    @FXML public void showSkillTree() { loadChamber("SkillTree.fxml"); }
    @FXML public void showArena() { loadChamber("Arena.fxml"); }
    @FXML public void showCVForge() { loadChamber("CVForge.fxml"); }
    @FXML public void showArchetype() { loadChamber("ArchetypeSelect.fxml"); }
    @FXML public void showArsenal() { loadChamber("ArsenalView.fxml"); }

    /**
     * FEDI-STANDARD DRY: The Controller does the drawing, the Core provides the screen!
     */
    private void loadChamber(String fxmlName) {
        try {
            IO.println("⚡ NAVIGATOR: Requesting " + fxmlName + " from Solanum Core...");

            // 1. Fetch the screen from the Quantum Moon Cache (returns Parent)
            Parent chamber = SolanumCore.getInstance().observe(fxmlName);

            if (chamber != null) {
                // 2. Start invisible
                chamber.setOpacity(0);

                // 3. Inject into the UI
                contentArea.getChildren().clear();
                contentArea.getChildren().add(chamber);

                // 4. Fade in gracefully
                FadeTransition ft = new FadeTransition(Duration.millis(400), chamber);
                ft.setToValue(1.0);
                ft.play();
            }
        } catch (Exception e) {
            IO.println("❌ Navigation Error: " + e.getMessage());
        }
    }
}