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

    @FXML private VBox targetedColumn;
    @FXML private VBox appliedColumn;
    @FXML private VBox interviewingColumn;
    @FXML private TextArea magicPasteArea;

    private ApplicationDAO appDAO = new ApplicationDAO();
    private JobOfferDAO jobDAO = new JobOfferDAO();
    private UserDAO userDAO = new UserDAO();
    private GeminiAIService oracle = new GeminiAIService();
    private ArsenalService arsenalService = new ArsenalService();

    @FXML
    public void initialize() {
        IO.println("⚔️ WAR ROOM: Hydrating Command Center from Database...");

        targetedColumn.setMinHeight(450.0);
        appliedColumn.setMinHeight(450.0);
        interviewingColumn.setMinHeight(450.0);

        // 1. THE HYDRATION ENGINE: Load everything from MySQL when the app opens!
        hydrateKanbanBoard();

        // 2. TEACH THE COLUMNS TO ACCEPT DRAGGING
        setupDropZone(targetedColumn, "TARGETED");
        setupDropZone(appliedColumn, "APPLIED");
        setupDropZone(interviewingColumn, "INTERVIEWING");
    }

    /**
     * FEDI-STANDARD HYDRATION: Clears the UI and rebuilds it from the Database.
     */
    private void hydrateKanbanBoard() {
        targetedColumn.getChildren().clear();
        appliedColumn.getChildren().clear();
        interviewingColumn.getChildren().clear();

        ArrayList<Application> allApps = appDAO.readAll();
        ArrayList<JobOffer> allJobs = jobDAO.readAll();

        for (Application app : allApps) {
            // Find the matching JobOffer for this Application
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
        IO.println("💧 Hydration Complete: Loaded " + allApps.size() + " cards from MySQL.");
    }

    /**
     * FEDI-STANDARD DRY: Creates the UI Card and attaches all Drag & Drop mechanics automatically.
     */
    private Label createJobCard(Application app, JobOffer job) {
        Label jobCard = new Label(job.getTitle() + " @ " + job.getCompany());

        // The ID of the UI element is literally the Application ID from the Database!
        String uniqueCardId = String.valueOf(app.getId());
        jobCard.setId(uniqueCardId);

        jobCard.setStyle("-fx-background-color: #21262d; -fx-text-fill: #58a6ff; -fx-padding: 15; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-font-weight: bold;");
        jobCard.setPrefWidth(260);

        jobCard.setOnMouseEntered(e -> jobCard.setStyle("-fx-background-color: #21262d; -fx-text-fill: #58a6ff; -fx-padding: 15; -fx-border-color: #58a6ff; -fx-border-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;"));
        jobCard.setOnMouseExited(e -> jobCard.setStyle("-fx-background-color: #21262d; -fx-text-fill: #58a6ff; -fx-padding: 15; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-font-weight: bold;"));

        // THE DRAG MECHANIC
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
     * FEDI-STANDARD DRY: Sets up any column to accept drops and trigger real data payloads.
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

                // TARGET 3: REAL DATA PAYLOAD
                // Find the exact Application, Job, and User from the Database
                Application activeApp = appDAO.readAll().stream().filter(a -> a.getId() == appId).findFirst().orElse(null);

                if (activeApp != null) {
                    JobOffer activeJob = jobDAO.readAll().stream().filter(j -> j.getId() == activeApp.getJobOfferId()).findFirst().orElse(null);
                    // Assuming User ID 1 is our main logged-in user for now
                    User activeUser = userDAO.readAll().stream().filter(u -> u.getId() == activeApp.getUserId()).findFirst().orElse(null);

                    if (activeJob != null && activeUser != null) {
                        // Update Database Status
                        appDAO.updateKanbanStatus(appId, targetStatus);
                        activeApp.setKanbanStatus(targetStatus);

                        // Re-hydrate the board to visually move the card
                        Platform.runLater(this::hydrateKanbanBoard);

                        // IF DROPPED IN APPLIED -> TRIGGER ARSENAL WITH REAL DATA!
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

        CompletableFuture.runAsync(() -> {
            try {
                IO.println("🧠 Asking Gemini to extract Job details...");
                String aiJsonResponse = oracle.extractJobDetails(rawText);

                if (aiJsonResponse.startsWith("ERROR")) {
                    throw new Exception(aiJsonResponse);
                }

                String cleanJson = aiJsonResponse.replace("```json", "").replace("```", "").trim();
                JSONObject jobData = new JSONObject(cleanJson);
                String extractedTitle = jobData.optString("title", "Unknown Title");
                String extractedCompany = jobData.optString("company", "Unknown Company");
                String extractedPain = jobData.optString("pain_point", "Needs AI optimization.");

                // 1. Create Job in DB
                JobOffer newJob = new JobOffer(extractedTitle, extractedCompany, rawText, extractedPain);
                jobDAO.create(newJob);

                // 2. Fetch the newly created Job ID (University workaround: grab the last one)
                ArrayList<JobOffer> allJobs = jobDAO.readAll();
                JobOffer savedJob = allJobs.get(allJobs.size() - 1);

                // 3. Create the Application Kanban Card in DB (Assuming User 1, CV 1)
                Application newApp = new Application(1, savedJob.getId(), 1, "TARGETED", "", "", "");
                appDAO.create(newApp);

                // 4. Visually update UI by re-hydrating!
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