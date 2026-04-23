package com.careerforge.controller;

// FEDI-STANDARD: Pointing to the TRUE 6-Dimensional Core!
import com.careerforge.core.SolanumCore;
import com.careerforge.model.User;
import com.careerforge.util.UserSession;
import javafx.fxml.FXML;

/**
 * // SARAH & FEDI: The Soul of the Destiny Gate.
 * // Calibrates the Oracle's personality and saves it to the User's Session.
 */
public class ArchetypeController {

    /*
     * FEYNMAN COMMENT: Imagine you are entering an RPG game.
     * Choosing "Tech" or "Business" sets your character's 'Class'.
     * We save this in UserSession so the Arsenal Service knows exactly
     * what kind of Cover Letter to generate later!
     */

    @FXML
    public void selectTech() {
        IO.println("💻 ARCHETYPE: Tech & Dev selected. Calibrating Oracle...");
        saveArchetype("Technology & Software Engineering");
    }

    @FXML
    public void selectBusiness() {
        IO.println("📊 ARCHETYPE: Business selected. Calibrating Oracle...");
        saveArchetype("Business & Strategic Management");
    }

    @FXML
    public void selectHealth() {
        IO.println("🩺 ARCHETYPE: Healthcare selected. Calibrating Oracle...");
        saveArchetype("Healthcare & Medical Sciences");
    }

    @FXML
    public void selectArt() {
        IO.println("🎨 ARCHETYPE: Creative selected. Calibrating Oracle...");
        saveArchetype("Creative Arts & Design");
    }

    private void saveArchetype(String archetype) {
        User activeUser = UserSession.getInstance().getActiveUser();
        if (activeUser != null) {
            activeUser.setTargetJobTitle(archetype);
            IO.println("✅ Destiny Locked: " + archetype);
        } else {
            IO.println("⚠️ Warning: No active user session. Proceeding as Guest.");
        }

        // Teleport the user to the CV Forge using the TRUE Core
        SolanumCore.getInstance().observe("CVForge.fxml");
    }
}