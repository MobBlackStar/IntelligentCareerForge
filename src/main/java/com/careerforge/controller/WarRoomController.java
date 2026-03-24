package com.careerforge.controller;

import com.careerforge.dao.ApplicationDAO;
import com.careerforge.dao.JobOfferDAO;
import com.careerforge.service.GeminiAIService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class WarRoomController {

    /*
     * FEYNMAN COMMENT: These VBoxes are our Kanban Columns!
     * Think of them like specialized buckets.
     * We will drop 'Cards' into these buckets.
     */
    @FXML private VBox targetedColumn;
    @FXML private VBox appliedColumn;
    @FXML private VBox interviewingColumn;
    @FXML private TextArea magicPasteArea; // For the "Magic Paste" job injection

    // Summoning the Backend Arsenal
    private ApplicationDAO appDAO = new ApplicationDAO();
    private JobOfferDAO jobDAO = new JobOfferDAO();
    private GeminiAIService oracle = new GeminiAIService();

    /**
     * // SARAH: This method runs automatically when the screen opens!
     */
    @FXML
    public void initialize() {
        System.out.println("⚔️ WAR ROOM: Initializing Command Center...");
        // Later, we will write code here to load all the user's jobs from the database!
    }

    /**
     * THE MAGIC PASTE (L'Oeil de l'Aigle)
     * // SARAH: This button takes a messy LinkedIn post and uses the AI to organize it!
     */
    @FXML
    public void handleMagicPaste() {
        String rawText = magicPasteArea.getText();
        if (rawText.isEmpty()) return;

        System.out.println("🦅 MAGIC PASTE: Eagle Eye activated. Parsing job...");

        // 1. Tell the Oracle to extract JSON from the mess
        // (For now, we simulate the success. Next, we link it to the real Gemini!)
        System.out.println("⏳ Calling Gemini to parse raw text...");

        // 2. Create a "Card" in the UI (A small glowing box for the job)
        Label jobCard = new Label("New Job @ LinkedIn");
        jobCard.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #00f2fe; -fx-padding: 15; -fx-border-color: #00f2fe; -fx-border-radius: 5;");
        jobCard.setPrefWidth(230);

        // 3. Drop it into the "Targeted" bucket!
        targetedColumn.getChildren().add(jobCard);

        magicPasteArea.clear();
        System.out.println("✅ Card added to TARGETED column.");
    }
}