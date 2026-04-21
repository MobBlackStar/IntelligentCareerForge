package com.careerforge.controller;

import com.careerforge.model.JobOffer;
import com.careerforge.model.User;
import com.careerforge.service.ArenaService;
import com.careerforge.util.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.concurrent.CompletableFuture;

public class ArenaController {

    // SARAH'S FXML VARIABLES
    @FXML private Label timerLabel;
    @FXML private Label questionDisplay;
    @FXML private TextArea answerArea;
    @FXML private Button actionButton;

    // FEDI'S BACKEND ENGINE
    private ArenaService arenaService = new ArenaService();
    private Timeline timeline;
    private int secondsRemaining = 60;
    private boolean isRunning = false;

    private String currentQuestion = "";
    private JobOffer targetJob;

    @FXML
    public void initialize() {
        IO.println("💀 THE ARENA: Initializing Pressure Cooker...");

        answerArea.setDisable(true);
        actionButton.setText("SUMMON THE CEO");

        // For testing, mock the job. In production, pull from SolanumCore/EventBus
        this.targetJob = new JobOffer("Senior Dev", "Google", "Code in Java", "Needs massive scale");

        setupTimer();
    }

    private void setupTimer() {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            this.secondsRemaining--;
            this.timerLabel.setText(String.valueOf(this.secondsRemaining));

            // SARAH'S GAMIFICATION: THE ADRENALINE PULSE
            if (this.secondsRemaining <= 10) {
                if (this.secondsRemaining % 2 == 0) {
                    this.timerLabel.setStyle("-fx-text-fill: white; -fx-scale-x: 1.5; -fx-scale-y: 1.5;");
                } else {
                    this.timerLabel.setStyle("-fx-text-fill: #ff0044; -fx-scale-x: 1.2; -fx-scale-y: 1.2;");
                }
                IO.println("⚠️ ALARM: " + this.secondsRemaining + " seconds left!");
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
        // Prevent spam clicking while loading
        actionButton.setDisable(true);
        questionDisplay.setText("Summoning the CEO... Prepare yourself.");
        questionDisplay.setStyle("-fx-border-color: #30363d; -fx-text-fill: #e6edf3;"); // Reset CSS
        IO.println("🔥 ARENA: Requesting brutal question from Oracle...");

        // FEDI'S ASYNC ENGINE
        CompletableFuture.runAsync(() -> {
            try {
                User activeUser = UserSession.getInstance().getActiveUser();
                if (activeUser == null) activeUser = new User("Fedi (Test)", "test@forge.com", "pass", "Dev");

                // Generate real question from Database/AI context
                currentQuestion = arenaService.generateBrutalQuestion(activeUser, targetJob);

                Platform.runLater(() -> {
                    this.isRunning = true;
                    this.secondsRemaining = 60;
                    this.timerLabel.setText("60");
                    this.timerLabel.setStyle("-fx-text-fill: #ff0044; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"); // Reset style

                    this.answerArea.setDisable(false);
                    this.answerArea.clear();

                    this.questionDisplay.setText(currentQuestion);

                    this.actionButton.setDisable(false);
                    this.actionButton.setText("SUBMIT BEFORE TIME OUT");

                    this.timeline.playFromStart();
                    IO.println("🔥 ARENA IGNITED.");
                });
            } catch (Exception e) {
                IO.println("❌ Arena failed to summon CEO: " + e.getMessage());
                Platform.runLater(() -> {
                    questionDisplay.setText("❌ Connection to CEO failed. Try again.");
                    actionButton.setDisable(false);
                });
            }
        });
    }

    private void handleEndArena(String initialMsg) {
        this.isRunning = false;
        this.timeline.stop();
        this.answerArea.setDisable(true);
        this.actionButton.setDisable(true); // Lock until graded
        this.questionDisplay.setText(initialMsg);
        IO.println(initialMsg);

        // FEDI'S ASYNC GRADING ENGINE
        CompletableFuture.runAsync(() -> {
            try {
                String userAnswer = answerArea.getText();
                String feedback = arenaService.evaluateAnswer(targetJob, currentQuestion, userAnswer);

                Platform.runLater(() -> {
                    this.questionDisplay.setText(feedback);
                    this.actionButton.setDisable(false);
                    this.actionButton.setText("RE-ENTER THE ARENA");

                    // SARAH'S CSS LOGIC
                    if (!feedback.contains("SCORE: 0") && !feedback.contains("SCORE: 1") && !feedback.contains("SCORE: 2") && !feedback.contains("SCORE: 3")) {
                        this.questionDisplay.setStyle("-fx-border-color: #58a6ff; -fx-text-fill: #58a6ff;");
                    } else {
                        this.questionDisplay.setStyle("-fx-border-color: #ff0044; -fx-text-fill: #ff0044;");
                    }
                });
            } catch (Exception e) {
                IO.println("❌ Oracle Judgment Failed: " + e.getMessage());
                Platform.runLater(() -> {
                    this.questionDisplay.setText("❌ The Oracle is silent. Try again.");
                    this.actionButton.setDisable(false);
                    this.actionButton.setText("RE-ENTER THE ARENA");
                });
            }
        });
    }
}