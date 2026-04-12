package com.careerforge.controller;

import com.careerforge.util.SolanumCore;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        // FEYNMAN COMMENT: We tell the Core "You are the manager of this specific empty space."
        SolanumCore.getInstance().setMainContent(contentArea);
        showWarRoom(); // Default page
    }

    @FXML public void showWarRoom() {
        System.out.println("🏠 NAVIGATOR: Loading WarRoom.fxml");
        SolanumCore.getInstance().observe("WarRoom.fxml");
    }

    @FXML public void showPhantomSandbox() {
        System.out.println("🧬 NAVIGATOR: Loading PhantomSandbox.fxml");
        SolanumCore.getInstance().observe("PhantomSandbox.fxml");
    }

    @FXML public void showSkillTree() {
        System.out.println("🌳 NAVIGATOR: Loading SkillTree.fxml");
        SolanumCore.getInstance().observe("SkillTree.fxml");
    }

    @FXML public void showArena() {
        System.out.println("🔥 NAVIGATOR: Loading Arena.fxml");
        SolanumCore.getInstance().observe("Arena.fxml");
    }

    @FXML public void showCVForge() {
        // 🛑 CHECK FILENAME: In your project, is it CVForge.fxml or CvForge.fxml?
        System.out.println("📝 NAVIGATOR: Loading CVForge.fxml");
        SolanumCore.getInstance().observe("CVForge.fxml");
    }

    @FXML public void showArchetype() {
        System.out.println("🎯 NAVIGATOR: Loading ArchetypeSelect.fxml");
        SolanumCore.getInstance().observe("ArchetypeSelect.fxml");
    }

    @FXML public void showArsenal() {
        System.out.println("🚀 NAVIGATOR: Loading ArsenalView.fxml");
        SolanumCore.getInstance().observe("ArsenalView.fxml");
    }
}