package com.careerforge.controller;

import com.careerforge.service.GeminiAIService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ArsenalController {

    @FXML private TextArea letterArea;
    @FXML private TextArea dmArea;

    private GeminiAIService oracle = new GeminiAIService();

    @FXML
    public void initialize() {
        generateNewArsenal();
    }

    @FXML
    public void generateNewArsenal() {
        letterArea.setText("⏳ THE ORACLE IS WRITING YOUR LETTER...");
        dmArea.setText("⏳ FORGING DM...");

        new Thread(() -> {
            try {
                // AI Prompt for the Cover Letter
                String letter = oracle.interrogateCV("Write a professional, 3-paragraph cover letter for a Software Engineer role at Google. Be concise and persuasive.");

                // AI Prompt for the DM
                String dm = oracle.interrogateCV("Write a 2-sentence LinkedIn DM to a recruiter for a Software Engineer role. Make it punchy.");

                Platform.runLater(() -> {
                    letterArea.setText(letter);
                    dmArea.setText(dm);
                    System.out.println("✅ ARSENAL: Weapons generated successfully.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    letterArea.setText("❌ Forge Error.");
                    dmArea.setText("❌ Forge Error.");
                });
            }
        }).start();
    }

    @FXML
    public void copyLetter() { copyToClipboard(letterArea.getText()); }

    @FXML
    public void copyDM() { copyToClipboard(dmArea.getText()); }

    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
        System.out.println("📋 Copied to system clipboard.");
    }
}