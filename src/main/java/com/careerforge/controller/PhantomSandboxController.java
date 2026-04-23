package com.careerforge.controller;

import com.careerforge.dao.CVDAO;
import com.careerforge.model.CV;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * // FEDI & SARAH: The Phantom ATS Sandbox.
 * // Blends the MySQL Leaderboard with Sarah's Gamified Pro-League CSS.
 */
public class PhantomSandboxController {

    @FXML private VBox leaderboardContainer;
    @FXML private Label insightDisplay;

    // FEDI-STANDARD: Connecting to the True Database
    private CVDAO cvDAO = new CVDAO();

    @FXML
    public void initialize() {
        IO.println("👻 PHANTOM SANDBOX: Initiating Live ATS Leaderboard...");
        loadCompetitors();
    }

    private void loadCompetitors() {
        leaderboardContainer.getChildren().clear();

        /*
         * FEYNMAN COMMENT: Async Loading
         * The database might take a fraction of a second to reply.
         * We load it in the background so the rest of the UI doesn't stutter.
         */
        CompletableFuture.runAsync(() -> {
            try {
                // Fetch REAL leaderboard for Job ID 1 (Testing scenario)
                ArrayList<CV> rankedCVs = cvDAO.getLeaderboardForJob(1);

                Platform.runLater(() -> {
                    int rank = 1;
                    for (CV cv : rankedCVs) {
                        // Dynamic Naming based on DB flags
                        String displayName = cv.isGhost() ? "Ghost: " + cv.getGhostName() : "YOU (The Challenger)";
                        String insight = cv.isGhost() ? "Master of ATS Optimization." : "Reviewing your missing skills...";

                        addCompetitor(String.valueOf(rank), displayName, cv.getAtsScore(), insight);
                        rank++;
                    }

                    if (rankedCVs.isEmpty()) {
                        addCompetitor("-", "NO DATA IN SECTOR", 0, "Run the SeedApp or inject a Job to begin.");
                    }

                    IO.println("✅ ATS Leaderboard Hydrated from Database.");
                });
            } catch (Exception e) {
                IO.println("❌ Phantom Sandbox Crash: " + e.getMessage());
            }
        });
    }

    /**
     * SARAH'S BEAUTIFUL UI DRAWING LOGIC (Preserved perfectly)
     */
    private void addCompetitor(String rank, String name, int score, String intel) {
        HBox card = new HBox(20);
        card.getStyleClass().add("leaderboard-card");

        if (name.contains("YOU")) {
            card.setStyle("-fx-border-color: #00f2fe; -fx-border-width: 2;");
        }

        // THE DIAMOND RANK
        StackPane diamond = new StackPane();
        diamond.getStyleClass().add("rank-diamond");
        Label rText = new Label(rank);
        rText.getStyleClass().add("rank-text");
        diamond.getChildren().add(rText);

        // INFO & SCORE BAR
        VBox info = new VBox(8);
        Label nLabel = new Label(name.toUpperCase());
        nLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // THE SCORE BAR
        AnchorPane scoreBarBg = new AnchorPane();
        scoreBarBg.setPrefSize(300, 8);
        scoreBarBg.getStyleClass().add("score-meter-bg");

        Region scoreFill = new Region();
        scoreFill.setPrefSize((score * 3.0), 8); // Scale width dynamically by DB Score!
        scoreFill.getStyleClass().add("score-meter-fill");

        scoreBarBg.getChildren().add(scoreFill);
        info.getChildren().addAll(nLabel, scoreBarBg);
        card.getChildren().addAll(diamond, info);

        card.setOnMouseClicked(e -> insightDisplay.setText("DATA ANALYSIS: " + intel));

        leaderboardContainer.getChildren().add(card);
    }

    @FXML
    public void handleAssimilate() {
        insightDisplay.setText("ASSIMILATING...[||||||||||] 100% - CV UPDATED.");
        IO.println("🧬 INITIATING SKILL HEIST ON RANK #1 GHOST...");
    }
}