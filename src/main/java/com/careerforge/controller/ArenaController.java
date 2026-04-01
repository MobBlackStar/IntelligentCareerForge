package com.careerforge.controller;

import com.careerforge.service.GeminiAIService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

public class ArenaController {

    @FXML private Label timerLabel;
    @FXML private Label questionDisplay;
    @FXML private TextArea answerArea;
    @FXML private Button actionButton;

    private GeminiAIService oracle = new GeminiAIService();
    private Timeline timeline;
    private int secondsRemaining = 60;
    private boolean isRunning = false;

    @FXML
    public void initialize() {
        // FEYNMAN COMMENT: We lock the keyboard until the user clicks START!
        answerArea.setDisable(true);
        setupTimer();
    }

    private void setupTimer() {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            this.secondsRemaining--;
            this.timerLabel.setText(String.valueOf(this.secondsRemaining));

            // THE ADRENALINE PULSE
            if (this.secondsRemaining <= 10) {
                // FEYNMAN COMMENT: We are changing the UI color every second
                // to make it look like a flashing alarm!
                if (this.secondsRemaining % 2 == 0) {
                    this.timerLabel.setStyle("-fx-text-fill: white; -fx-scale-x: 1.5; -fx-scale-y: 1.5;");
                } else {
                    this.timerLabel.setStyle("-fx-text-fill: #ff0044; -fx-scale-x: 1.2; -fx-scale-y: 1.2;");
                }
            }

            if (this.secondsRemaining <= 0) {
                handleEndArena("💥 SYSTEM FAILURE: You were too slow.");
            }
        }));
        this.timeline.setCycleCount(60);
    }
    @FXML
    public void handleArenaAction() {
        if (!this.isRunning) {
            startArena();
        } else {
            handleEndArena("⌛ SUBMITTED. The Oracle is grading your survival...");
        }
    }

    private void startArena() {
        this.isRunning = true;
        this.secondsRemaining = 60;
        this.answerArea.setDisable(false);
        this.answerArea.clear();
        this.actionButton.setText("SUBMIT BEFORE TIME OUT");

        this.questionDisplay.setText("CEO: 'We noticed your CV claims technical mastery. Explain in 3 sentences how you would handle a complete server crash on your first day.'");

        this.timeline.playFromStart();
        System.out.println("🔥 ARENA IGNITED.");
    }

    private void handleEndArena(String initialMsg) {
        this.isRunning = false;
        this.timeline.stop();
        this.answerArea.setDisable(true);
        this.actionButton.setText("RE-ENTER THE ARENA");
        this.questionDisplay.setText("⌛ THE ORACLE IS WEIGHING YOUR SOUL...");

        // FEYNMAN COMMENT: We send your answer to the AI in a background thread.
        // It's like sending a letter to a judge and waiting for the verdict!
        new Thread(() -> {
            try {
                String userAnswer = answerArea.getText();
                String prompt = "Review this interview answer: '" + userAnswer +
                        "'. Give a survival score (0-100) and 1 sentence of brutal feedback.";

                String feedback = oracle.interrogateCV(prompt);

                // Update the UI with the real grade!
                javafx.application.Platform.runLater(() -> {
                    this.questionDisplay.setText(feedback);
                    // If you survived (Score > 70), make it glow blue. If you died, keep it red.
                    if (!feedback.contains("0") && !feedback.contains("1") && !feedback.contains("2")) {
                        this.questionDisplay.setStyle("-fx-border-color: #58a6ff; -fx-text-fill: #58a6ff;");
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> this.questionDisplay.setText("❌ The Oracle is silent. Try again."));
            }
        }).start();
    }
}