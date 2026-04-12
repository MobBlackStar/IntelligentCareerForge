package com.careerforge.controller;

import com.careerforge.service.GeminiAIService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CVForgeController {

    // --- 1. FORM INPUTS ---
    @FXML private TextField nameInput, emailInput, phoneInput, educationInput;
    @FXML private TextArea skillsInput, projectsInput;

    // --- 2. PREVIEW PAPER COMPONENTS ---
    @FXML private VBox previewPaper;
    @FXML private Label previewName, previewContact, previewSkills, previewEducation, previewProjects;

    // --- 3. THE GHOST ADVISOR ---
    @FXML private Label advisorLabel;

    private GeminiAIService oracle = new GeminiAIService();

    @FXML
    public void initialize() {
        // A. Immediate Name Binding
        nameInput.textProperty().addListener((obs, oldVal, newVal) ->
                previewName.setText(newVal.toUpperCase()));

        // B. Contact Info Consolidation
        emailInput.textProperty().addListener((obs, oldVal, newVal) -> updateContact());
        phoneInput.textProperty().addListener((obs, oldVal, newVal) -> updateContact());

        // C. Body Content Binding (Wired to the Ghost Advisor!)
        skillsInput.textProperty().addListener((obs, oldVal, newVal) -> previewSkills.setText(newVal));
        educationInput.textProperty().addListener((obs, oldVal, newVal) -> previewEducation.setText(newVal));

        projectsInput.textProperty().addListener((obs, oldVal, newVal) -> {
            previewProjects.setText(newVal);
            checkWordQuality(newVal); // Trigger the Ghost Advisor on the Projects box!
        });

        System.out.println("✨ FORGE: Live binding initialized.");
    }

    private void updateContact() {
        previewContact.setText(emailInput.getText() + " | " + phoneInput.getText());
    }

    private void checkWordQuality(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("made") || lower.contains("helped") || lower.contains("worked on")) {
            advisorLabel.setText("💡 ORACLE: 'Made' is weak. Use 'Engineered' or 'Orchestrated'!");
            advisorLabel.setStyle("-fx-text-fill: #facc15; -fx-font-style: italic;");
        } else if (text.length() > 10) {
            advisorLabel.setText("✨ Oracle is satisfied with your metrics.");
            advisorLabel.setStyle("-fx-text-fill: #58a6ff;");
        }
    }

    // --- THE TEMPLATE MATRIX ---

    @FXML
    public void applyModernTemplate() {
        previewPaper.getStyleClass().removeAll("template-minimal", "template-professional");
        previewPaper.getStyleClass().add("template-modern");
        resetPaperFonts("Segoe UI", "#58a6ff");
        System.out.println("🎨 FORGE: Modern Style Active.");
    }

    @FXML
    public void applyMinimalTemplate() {
        previewPaper.getStyleClass().removeAll("template-modern", "template-professional");
        previewPaper.getStyleClass().add("template-minimal");
        resetPaperFonts("System", "#30363d");
        System.out.println("🎨 FORGE: Minimal Style Active.");
    }

    @FXML
    public void applyProfessionalTemplate() {
        previewPaper.getStyleClass().removeAll("template-modern", "template-minimal");
        previewPaper.getStyleClass().add("template-professional");
        previewName.setStyle("-fx-font-family: 'Times New Roman'; -fx-text-fill: black; -fx-font-size: 28px;");
        System.out.println("🏛️ FORGE: Professional Elite Style Active.");
    }

    private void resetPaperFonts(String family, String color) {
        previewName.setStyle("-fx-font-family: '" + family + "'; -fx-text-fill: #1a1a2e;");
    }

    @FXML
    public void handleSaveCV() {
        System.out.println("💾 FORGE: Exporting Master Engram to Database...");
    }
}