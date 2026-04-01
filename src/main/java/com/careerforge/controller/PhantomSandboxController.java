package com.careerforge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class PhantomSandboxController {

    @FXML private VBox leaderboardContainer;
    @FXML private Label insightDisplay;

    @FXML
    public void initialize() {
        loadCompetitors();
    }

    private void loadCompetitors() {
        leaderboardContainer.getChildren().clear();
        addCompetitor("1", "Ghost: Nour (Architect)", 98, "Uses 'Distributed Systems' and 'CI/CD' keywords.");
        addCompetitor("2", "Ghost: Amine (Growth)", 85, "Strong focus on 'SaaS Metrics'.");
        addCompetitor("3", "YOU (The Challenger)", 72, "Missing 'AWS' and 'Docker' certifications.");
        addCompetitor("4", "Ghost: Junior_Bot", 40, "Weak power verbs.");
    }

    private void addCompetitor(String rank, String name, int score, String intel) {
        HBox card = new HBox(20);
        card.getStyleClass().add("leaderboard-card");
        if (name.contains("YOU")) card.setStyle("-fx-border-color: #00f2fe; -fx-border-width: 2;");

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

        // THE SCORE BAR (Game UI trick!)
        AnchorPane scoreBarBg = new AnchorPane();
        scoreBarBg.setPrefSize(300, 8);
        scoreBarBg.getStyleClass().add("score-meter-bg");

        Region scoreFill = new Region();
        scoreFill.setPrefSize((score * 3), 8); // Scale the width by score
        scoreFill.getStyleClass().add("score-meter-fill");

        scoreBarBg.getChildren().add(scoreFill);

        info.getChildren().addAll(nLabel, scoreBarBg);
        card.getChildren().addAll(diamond, info);

        card.setOnMouseClicked(e -> insightDisplay.setText("DATA ANALYSIS: " + intel));

        leaderboardContainer.getChildren().add(card);
    }

    @FXML
    public void handleAssimilate() {
        insightDisplay.setText("ASSIMILATING... [||||||||||] 100% - CV UPDATED.");
    }
}