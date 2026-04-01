package com.careerforge.controller;

import com.careerforge.dao.JobOfferDAO;
import com.careerforge.model.JobOffer;
import com.careerforge.service.GeminiAIService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

public class WarRoomController {

    @FXML private VBox targetedColumn;
    @FXML private TextArea magicPasteArea;

    private GeminiAIService oracle = new GeminiAIService();
    private JobOfferDAO jobDAO = new JobOfferDAO();

    @FXML
    public void handleMagicPaste() {
        String rawText = magicPasteArea.getText();
        if (rawText.isEmpty()) return;

        // 1. Create a "Loading" card in Sarah's Style
        Label jobCard = new Label("🔍 ANALYZING...");
        jobCard.getStyleClass().add("job-card");
        jobCard.setPrefWidth(240);
        targetedColumn.getChildren().add(jobCard);
        magicPasteArea.clear();

        // 2. Background Task (AI + Database)
        new Thread(() -> {
            try {
                // Ask AI to extract details
                String aiResult = oracle.extractJobDetails(rawText);
                // aiResult will be JSON or text: "Title @ Company"

                // SAVE TO DATABASE (Fedi's Layer)
                JobOffer newJob = new JobOffer(aiResult, "Extracted", rawText, "Unknown");
                jobDAO.create(newJob);

                // 3. Update UI
                Platform.runLater(() -> {
                    jobCard.setText(aiResult);
                    System.out.println("✅ WAR ROOM: Job saved and card updated.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> jobCard.setText("❌ OFFLINE"));
            }
        }).start();
    }
}