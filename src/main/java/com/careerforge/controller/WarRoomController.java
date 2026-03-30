package com.careerforge.controller;

import com.careerforge.dao.ApplicationDAO;
import com.careerforge.dao.JobOfferDAO;
import com.careerforge.service.GeminiAIService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

public class WarRoomController {

    @FXML private VBox targetedColumn;
    @FXML private VBox appliedColumn;
    @FXML private VBox interviewingColumn;
    @FXML private TextArea magicPasteArea;

    private ApplicationDAO appDAO = new ApplicationDAO();
    private JobOfferDAO jobDAO = new JobOfferDAO();
    private GeminiAIService oracle = new GeminiAIService();

    @FXML
    public void handleMagicPaste() {
        String rawText = magicPasteArea.getText();
        if (rawText.isEmpty()) return;

        // 1. Create the card immediately with a "Loading" status
        Label jobCard = new Label("🔍 AI is analyzing...");
        jobCard.getStyleClass().add("job-card");
        jobCard.setPrefWidth(250);
        jobCard.setWrapText(true);

        targetedColumn.getChildren().add(jobCard);
        magicPasteArea.clear();

        // 2. Call the AI in the background so the UI doesn't freeze
        new Thread(() -> {
            try {
                // Ask Gemini to extract the Job and Company
                String aiResult = oracle.interrogateCV("Briefly extract ONLY 'Job Title @ Company' from this: " + rawText);

                // 3. Update the card and enable dragging
                javafx.application.Platform.runLater(() -> {
                    jobCard.setText(aiResult);
                    setupDragAndDrop(jobCard); // THIS IS THE METHOD THE ERROR COULDN'T FIND
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> jobCard.setText("❌ AI extraction failed"));
            }
        }).start();
    }

    /**
     * // SARAH: This is the actual "Hands" of the controller.
     * // This defines how a card is picked up and dropped into the Yellow column.
     */
    private void setupDragAndDrop(Label card) {
        // START DRAGGING
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(card.getText());
            db.setContent(content);
            event.consume();
        });

        // ALLOW DROPPING ON THE APPLIED COLUMN
        appliedColumn.setOnDragOver(event -> {
            if (event.getGestureSource() != appliedColumn && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // FINISH THE DROP
        appliedColumn.setOnDragDropped(event -> {
            if (event.getDragboard().hasString()) {
                VBox currentParent = (VBox) card.getParent();
                currentParent.getChildren().remove(card);
                appliedColumn.getChildren().add(card);

                System.out.println("🚀 MISSION PROGRESS: Job moved to APPLIED!");
                event.setDropCompleted(true);
            }
            event.consume();
        });
    }
}