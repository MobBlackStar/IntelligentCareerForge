package com.careerforge.controller;

import com.careerforge.core.SolanumCore;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * // FEDI: The Master Controller of the Living Aquarium.
 * // It dynamically builds the sidebar and breathes life into the Reactor Core.
 */
public class DashboardController {

    @FXML private VBox sidebarMenu;
    @FXML private StackPane reactorCore;

    @FXML
    public void initialize() {
        IO.println("🌌 DASHBOARD: Initializing the Solanum Shell...");

        // 1. Generate the Auto-Sidebar
        buildModuleMatrix();

        // 2. Inject the default starting room
        loadChamber("WarRoom.fxml");
    }

    /**
     * THE MODULE MATRIX
     * Instead of Sarah hardcoding buttons in FXML, we generate them dynamically!
     * If you invent a new feature next week, you just add one line here.
     */
    private void buildModuleMatrix() {
        sidebarMenu.getChildren().clear();

        sidebarMenu.getChildren().add(createNavButton("⚔️ War Room", "WarRoom.fxml"));
        sidebarMenu.getChildren().add(createNavButton("🌳 Skill Tree", "SkillTree.fxml"));
        sidebarMenu.getChildren().add(createNavButton("💀 The Arena", "Arena.fxml"));
        sidebarMenu.getChildren().add(createNavButton("👻 Phantom Sandbox", "PhantomSandbox.fxml"));
    }

    /**
     * FEDI-STANDARD DRY: Creates a beautiful, animated Neon Button out of thin air.
     */
    private Button createNavButton(String title, String fxmlName) {
        Button btn = new Button("  " + title);
        btn.setPrefWidth(250);
        btn.setPrefHeight(50);

        // Default State (Dimmed)
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #a0a0b5; -fx-font-size: 16px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 40;";
        // Hover State (Neon Fedi-Standard)
        String hoverStyle = "-fx-background-color: #1a1a2e; -fx-text-fill: #00f2fe; -fx-font-size: 16px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 40; -fx-cursor: hand; -fx-border-color: #00f2fe; -fx-border-width: 0 0 0 5;";

        btn.setStyle(defaultStyle);

        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));

        // When clicked, breathe the new chamber into the Reactor Core!
        btn.setOnAction(e -> loadChamber(fxmlName));

        return btn;
    }

    /**
     * THE BREATH OF LIFE: Uses the Solanum Core to fetch the screen, then gracefully fades it in.
     */
    private void loadChamber(String fxmlName) {
        try {
            IO.println("⚡ INJECTING CORE: Materializing " + fxmlName + "...");

            // The Solanum Core pulls it instantly from RAM (Zero lag!)
            Parent chamber = SolanumCore.getInstance().observe(fxmlName);

            if (chamber != null) {
                // Set it invisible
                chamber.setOpacity(0);

                // Clear the old screen and inject the new one
                reactorCore.getChildren().clear();
                reactorCore.getChildren().add(chamber);

                // Animate the fade-in (400 milliseconds)
                FadeTransition ft = new FadeTransition(Duration.millis(400), chamber);
                ft.setToValue(1.0);
                ft.play();
            }
        } catch (Exception e) {
            IO.println("❌ Core Injection Failed: " + e.getMessage());
        }
    }
}