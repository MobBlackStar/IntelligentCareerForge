package com.careerforge.controller;

import com.careerforge.model.User;
import com.careerforge.service.GeminiAIService;
import com.careerforge.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.concurrent.CompletableFuture;

/**
 * // FEDI & SARAH: The 1-Click Arsenal.
 * // Generates bespoke Cover Letters and DMs asynchronously.
 */
public class ArsenalController {

    @FXML private TextArea letterArea;
    @FXML private TextArea dmArea;

    private GeminiAIService oracle = new GeminiAIService();

    @FXML
    public void initialize() {
        IO.println("🚀 THE ARSENAL: Weapon systems online. Waiting for user command.");
    }

    @FXML
    public void generateNewArsenal() {
        letterArea.setText("⏳ THE ORACLE IS WRITING YOUR LETTER...");
        dmArea.setText("⏳ FORGING DM...");

        /*
         * FEYNMAN COMMENT: CompletableFuture (The God-Tier Thread)
         * We do not use "new Thread()". We use CompletableFuture because it is
         * deeply optimized by the JVM for API calls, preventing UI freezes.
         */
        CompletableFuture.runAsync(() -> {
            try {
                // Fetch the active user's destiny (Archetype) from the Session
                User activeUser = UserSession.getInstance().getActiveUser();
                String targetJob = (activeUser != null && activeUser.getTargetJobTitle() != null)
                        ? activeUser.getTargetJobTitle()
                        : "Software Engineer";

                IO.println("🧠 Prompting Oracle for dynamic target: " + targetJob);

                // AI Prompts injected with dynamic variables
                String letterPrompt = "Write a professional, 3-paragraph cover letter for a " + targetJob + " role. Be concise and persuasive.";
                String dmPrompt = "Write a 2-sentence LinkedIn DM to a recruiter for a " + targetJob + " role. Make it punchy.";

                String letter = oracle.interrogateCV(letterPrompt);
                String dm = oracle.interrogateCV(dmPrompt);

                // Safe UI Update
                Platform.runLater(() -> {
                    letterArea.setText(letter);
                    dmArea.setText(dm);
                    IO.println("✅ ARSENAL: Weapons generated successfully.");
                });

            } catch (Exception e) {
                IO.println("❌ Arsenal Forge Error: " + e.getMessage());
                Platform.runLater(() -> {
                    letterArea.setText("❌ Oracle Connection Failed.");
                    dmArea.setText("❌ Oracle Connection Failed.");
                });
            }
        });
    }

    @FXML
    public void copyLetter() { copyToClipboard(letterArea.getText()); }

    @FXML
    public void copyDM() { copyToClipboard(dmArea.getText()); }

    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
        IO.println("📋 Copied to system clipboard.");
    }
}