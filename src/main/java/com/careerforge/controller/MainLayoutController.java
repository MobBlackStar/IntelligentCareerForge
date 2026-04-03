package com.careerforge.controller;

import com.careerforge.util.SolanumCore;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        // FEYNMAN COMMENT: We tell the Core "You are the manager of this specific empty space."
        // Now, whenever we tell the core to "Observe" a file, it puts it here!
        SolanumCore.getInstance().setMainContent(contentArea);

        // Auto-load the War Room on startup
        showWarRoom();
    }

    @FXML public void showWarRoom() {
        SolanumCore.getInstance().observe("WarRoom.fxml");
    }

    @FXML public void showPhantomSandbox() {
        SolanumCore.getInstance().observe("PhantomSandbox.fxml");
    }

    @FXML public void showSkillTree() {
        SolanumCore.getInstance().observe("SkillTree.fxml");
    }

    @FXML public void showArena() {
        SolanumCore.getInstance().observe("Arena.fxml");
    }
}