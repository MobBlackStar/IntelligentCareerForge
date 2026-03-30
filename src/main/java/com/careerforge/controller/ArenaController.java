package com.careerforge.controller;

import com.careerforge.model.JobOffer;
import com.careerforge.model.User;
import com.careerforge.service.ArenaService;
import com.careerforge.util.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.util.concurrent.CompletableFuture;

public class ArenaController {

    @FXML private Label timerLabel;
    @FXML private Label questionLabel;
    @FXML private TextArea answerArea;
    @FXML private Button submitButton;
    @FXML private Label judgmentLabel;

    private ArenaService arenaService = new ArenaService();
    private Timeline countdown;
    private int secondsRemaining = 60;

    // We store the generated question so we can send it back for grading
    private String currentQuestion = "";
    private JobOffer targetJob;

    @FXML
    public void initialize() {
        IO.println("💀 THE ARENA: Initializing Pressure Cooker...");

        // For testing purposes, we hardcode a job. In production, we pass the JobOffer from the Kanban board.
        this.targetJob = new JobOffer("Senior Dev", "Google", "Code in Java", "Needs massive scale");

        lockArena("Summoning the CEO... Prepare yourself.");

        // Fetch the brutal question asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                User activeUser = UserSession.getInstance().getActiveUser();
                if (activeUser == null) activeUser = new User("Fedi (Test)", "test@forge.com", "pass", "Dev");

                currentQuestion = arenaService.generateBrutalQuestion(activeUser, targetJob);

                Platform.runLater(() -> {
                    questionLabel.setText(currentQuestion);
                    unlockArena();
                    startTimer();
                });
            } catch (Exception e) {
                IO.println("❌ Arena failed to summon CEO: " + e.getMessage());
            }
        });
    }

    /**
     * FEDI-STANDARD GAMIFICATION: The 60-Second Panic Timer
     */
    private void startTimer() {
        secondsRemaining = 60;
        timerLabel.setText("60");
        timerLabel.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold; -fx-font-size: 24;");

        // Creates a loop that runs every 1 second
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            timerLabel.setText(String.valueOf(secondsRemaining));

            // Psychological UI changes as time runs out
            if (secondsRemaining == 15) {
                timerLabel.setStyle("-fx-text-fill: #ff0000; -fx-font-weight: bold; -fx-font-size: 30;");
                IO.println("⚠️ 15 SECONDS REMAINING! The pressure is rising.");
            }

            if (secondsRemaining <= 0) {
                countdown.stop();
                handleTimeUp();
            }
        }));

        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    private void handleTimeUp() {
        IO.println("💀 TIME IS UP! You failed to answer the CEO.");
        lockArena("TIME IS UP. The CEO walks away.");
        judgmentLabel.setText("SCORE: 0/100\nYou froze under pressure.");
        judgmentLabel.setStyle("-fx-text-fill: #ff0044; -fx-font-weight: bold;");
    }

    @FXML
    public void handleSubmit() {
        String answer = answerArea.getText();
        if (answer.isEmpty()) return;

        IO.println("🛡️ Candidate submits answer with " + secondsRemaining + " seconds left!");
        countdown.stop();
        lockArena("The CEO is judging your response...");

        CompletableFuture.runAsync(() -> {
            try {
                String judgment = arenaService.evaluateAnswer(targetJob, currentQuestion, answer);

                Platform.runLater(() -> {
                    judgmentLabel.setText(judgment);

                    // Simple check to color the UI based on score
                    if (judgment.contains("SCORE: 100") || judgment.contains("SCORE: 9") || judgment.contains("SCORE: 8")) {
                        judgmentLabel.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold;"); // Green for pass
                    } else {
                        judgmentLabel.setStyle("-fx-text-fill: #ff0044; -fx-font-weight: bold;"); // Red for fail
                    }
                });
            } catch (Exception e) {
                IO.println("❌ Oracle Judgment Failed: " + e.getMessage());
            }
        });
    }

    private void lockArena(String message) {
        answerArea.setDisable(true);
        submitButton.setDisable(true);
        judgmentLabel.setText(message);
        judgmentLabel.setStyle("-fx-text-fill: #a0a0b5;");
    }

    private void unlockArena() {
        answerArea.setDisable(false);
        submitButton.setDisable(false);
        judgmentLabel.setText("");
    }
}