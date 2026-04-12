package com.careerforge.controller;

import com.careerforge.util.SolanumCore;
import javafx.fxml.FXML;

/**
 * // SARAH: This is the Soul of the Destiny Gate.
 * // It calibrates the Oracle's personality based on the user's choice.
 */
public class ArchetypeController {

    /*
     * FEYNMAN COMMENT: Imagine you are entering a RPG game.
     * Choosing "Tech" or "Business" sets your character's 'Class'.
     * This choice will be stored in the Solanum Core so every other room
     * (like the CV Forge) knows how to talk to you!
     */

    @FXML
    public void selectTech() {
        System.out.println("💻 ARCHETYPE: Tech & Dev selected. Calibrating Oracle for Technical Supremacy...");
        navigateToForge();
    }

    @FXML
    public void selectBusiness() {
        System.out.println("📊 ARCHETYPE: Business selected. Calibrating Oracle for Strategic ROI...");
        navigateToForge();
    }

    @FXML
    public void selectHealth() {
        System.out.println("🩺 ARCHETYPE: Healthcare selected. Calibrating Oracle for Precision...");
        navigateToForge();
    }

    @FXML
    public void selectArt() {
        System.out.println("🎨 ARCHETYPE: Creative selected. Calibrating Oracle for Aesthetic Impact...");
        navigateToForge();
    }

    private void navigateToForge() {
        // Once the choice is made, teleport the user directly to the Resume Builder!
        SolanumCore.getInstance().observe("CVForge.fxml");
    }
}