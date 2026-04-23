package com.careerforge.controller;

import com.careerforge.dao.ApplicationDAO;
import com.careerforge.dao.JobOfferDAO;
import com.careerforge.dao.UserDAO;
import com.careerforge.model.Application;
import com.careerforge.model.JobOffer;
import com.careerforge.model.User;
import com.careerforge.service.ArsenalService;
import com.careerforge.service.GeminiAIService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class WarRoomController {

    // UI Hooks to the FXML
    @FXML private VBox targetedColumn;
    @FXML private VBox appliedColumn;
    @FXML private VBox interviewingColumn;
    @FXML private TextArea magicPasteArea;

    // Database and Logic Engines
    private ApplicationDAO appDAO = new ApplicationDAO();
    private JobOfferDAO jobDAO = new JobOfferDAO();
    private UserDAO userDAO = new UserDAO();
    private GeminiAIService oracle = new GeminiAIService();
    private ArsenalService arsenalService = new ArsenalService();

    @FXML
    public void initialize() {
        IO.println("⚔️ WAR ROOM: Hydrating Command Center from Database...");

        // INVISIBLE BUCKET FIX: Ensures empty columns can still receive dragged cards
        targetedColumn.setMinHeight(450.0);
        appliedColumn.setMinHeight(450.0);
        interviewingColumn.setMinHeight(450.0);

        hydrateKanbanBoard();

        // Turn the UI VBoxes into actual Drop Zones
        setupDropZone(targetedColumn, "TARGETED");
        setupDropZone(appliedColumn, "APPLIED");
        setupDropZone(interviewingColumn, "INTERVIEWING");
    }

    /**
     * Loads jobs from the database and rebuilds the UI automatically.
     */
    private void hydrateKanbanBoard() {
        targetedColumn.getChildren().clear();
        appliedColumn.getChildren().clear();
        interviewingColumn.getChildren().clear();

        ArrayList<Application> allApps = appDAO.readAll();
        ArrayList<JobOffer> allJobs = jobDAO.readAll();

        for (Application app : allApps) {
            JobOffer matchedJob = null;
            for (JobOffer job : allJobs) {
                if (job.getId() == app.getJobOfferId()) {
                    matchedJob = job;
                    break;
                }
            }

            if (matchedJob != null) {
                Label card = createJobCard(app, matchedJob);
                switch (app.getKanbanStatus()) {
                    case "TARGETED" -> targetedColumn.getChildren().add(card);
                    case "APPLIED" -> appliedColumn.getChildren().add(card);
                    case "INTERVIEWING" -> interviewingColumn.getChildren().add(card);
                }
            }
        }
    }

    /**
     * Creates the physical Card and attaches the Drag mechanic.
     */
    private Label createJobCard(Application app, JobOffer job) {
        Label jobCard = new Label(job.getTitle() + " @ " + job.getCompany());

        // We hide the Database ID inside the UI element so we can find it later!
        String uniqueCardId = String.valueOf(app.getId());
        jobCard.setId(uniqueCardId);

        // Sarah's CSS class
        jobCard.getStyleClass().add("job-card");
        jobCard.setPrefWidth(260);

        /*
         * FEYNMAN COMMENT: The Drag Mechanic
         * When the user clicks and drags, we package the Card's ID into a "Clipboard".
         * The OS holds this clipboard while the mouse moves.
         */
        jobCard.setOnDragDetected(event -> {
            IO.println("🖱️ Picked up Application ID: " + uniqueCardId);
            Dragboard db = jobCard.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(uniqueCardId);
            db.setContent(content);
            event.consume();
        });

        return jobCard;
    }

    /**
     * Wires a column to accept a falling card and update the database.
     */
    private void setupDropZone(VBox column, String targetStatus) {
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });

        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                int appId = Integer.parseInt(db.getString());
                Application activeApp = appDAO.readAll().stream().filter(a -> a.getId() == appId).findFirst().orElse(null);

                if (activeApp != null) {
                    JobOffer activeJob = jobDAO.readAll().stream().filter(j -> j.getId() == activeApp.getJobOfferId()).findFirst().orElse(null);
                    User activeUser = userDAO.readAll().stream().filter(u -> u.getId() == activeApp.getUserId()).findFirst().orElse(null);

                    if (activeJob != null && activeUser != null) {
                        // 1. Save new status to database
                        appDAO.updateKanbanStatus(appId, targetStatus);
                        activeApp.setKanbanStatus(targetStatus);

                        // 2. Visually snap the card into place
                        Platform.runLater(this::hydrateKanbanBoard);

                        // 3. ARSENAL TRIGGER: If they dropped it into APPLIED, forge the Cover Letter!
                        if (targetStatus.equals("APPLIED")) {
                            IO.println("🔥 REAL CARD DROPPED IN APPLIED! Unleashing Arsenal for: " + activeJob.getCompany());
                            arsenalService.generateDeploymentPackage(activeUser, activeJob, activeApp);
                        }
                        success = true;
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    public void handleMagicPaste() {
        String rawText = magicPasteArea.getText();
        if (rawText.isEmpty()) return;

        IO.println("🦅 MAGIC PASTE: Eagle Eye activated.");
        magicPasteArea.setText("Oracle is analyzing the messy text... Please wait.");
        magicPasteArea.setDisable(true);

        /*
         * FEYNMAN COMMENT: Async Execution
         * If we ran this AI call on the main thread, the entire JavaFX window would
         * freeze and turn white while waiting for Google.
         * CompletableFuture sends the task to a background worker.
         */
        CompletableFuture.runAsync(() -> {
            try {
                IO.println("🧠 Asking Gemini to extract Job details...");
                String aiJsonResponse = oracle.extractJobDetails(rawText);

                if (aiJsonResponse.startsWith("ERROR")) {
                    throw new Exception(aiJsonResponse);
                }

                // Parse JSON
                String cleanJson = aiJsonResponse.replace("```json", "").replace("```", "").trim();
                JSONObject jobData = new JSONObject(cleanJson);
                String extractedTitle = jobData.optString("title", "Unknown Title");
                String extractedCompany = jobData.optString("company", "Unknown Company");
                String extractedPain = jobData.optString("pain_point", "Needs AI optimization.");

                // Save Job and App to DB
                JobOffer newJob = new JobOffer(extractedTitle, extractedCompany, rawText, extractedPain);
                jobDAO.create(newJob);

                ArrayList<JobOffer> allJobs = jobDAO.readAll();
                JobOffer savedJob = allJobs.get(allJobs.size() - 1);

                Application newApp = new Application(1, savedJob.getId(), 1, "TARGETED", "", "", "");
                appDAO.create(newApp);

                // Update UI safely
                Platform.runLater(() -> {
                    hydrateKanbanBoard();
                    magicPasteArea.clear();
                    magicPasteArea.setDisable(false);
                    IO.println("✅ UI Hydrated with new AI Card.");
                });

            } catch (Exception e) {
                IO.println("❌ AI Magic Paste Failed: " + e.getMessage());
                Platform.runLater(() -> {
                    magicPasteArea.setText("Error. Ensure API key is set and try again.");
                    magicPasteArea.setDisable(false);
                });
            }
        });
    }
}