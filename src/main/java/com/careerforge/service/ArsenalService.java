package com.careerforge.service;

import com.careerforge.dao.ApplicationDAO;
import com.careerforge.model.Application;
import com.careerforge.model.JobOffer;
import com.careerforge.model.User;
import java.util.concurrent.CompletableFuture;

/**
 * // FEDI: The 1-Click Arsenal Generator.
 * // SARAH: Call generateDeploymentPackage() when the user drops a card into the "APPLIED" column!
 */
public class ArsenalService {

    private GeminiAIService oracle = new GeminiAIService();
    private PdfTrojanService pdfForger = new PdfTrojanService();
    private ApplicationDAO applicationDAO = new ApplicationDAO();
    private SkillAnalyzerService skillAnalyzer = new SkillAnalyzerService();

    /**
     * Executes the heavy backend logic in a background thread so Sarah's UI doesn't freeze.
     */
    public void generateDeploymentPackage(User user, JobOffer job, Application appCard) {

        IO.println("⚙️ ARSENAL SERVICE: Initiating Deployment Package for " + job.getCompany() + "...");

        CompletableFuture.runAsync(() -> {
            try {
                // 1. Generate Custom Cover Letter via Oracle
                IO.println("🧠 Oracle is drafting the Cover Letter...");
                String promptCL = "Write a short, highly aggressive, elite Cover Letter for " + user.getName() +
                        " applying to " + job.getCompany() + " for the role of " + job.getTitle() +
                        ". Address their pain point: " + job.getCompanyPainPoint() + ". No fluff. 3 paragraphs max.";
                // We use our existing callGemini logic (you would add a raw prompt method in GeminiAIService, or we simulate it here)
                String coverLetter = oracle.interrogateCV(promptCL); // Reusing the interrogate channel for a direct prompt

                // 2. Generate 30-Day Action Plan
                IO.println("🧠 Oracle is drafting the 30-Day Trojan Action Plan...");
                String promptPlan = "Write a 30-Day Action Plan for " + job.getTitle() + " at " + job.getCompany() +
                        ". Format it beautifully. Week 1, Week 2, Week 3, Week 4.";
                String actionPlan = oracle.interrogateCV(promptPlan);

                // 3. Forge the Physical PDF
                IO.println("🖨️ Forging the Trojan PDF...");
                String fileName = user.getName().replace(" ", "_") + "_" + job.getCompany() + "_Application";
                // For the CV content, we use a placeholder or the Master CV from the database
                String simulatedCV = "Fedi-Standard FXML CV Content dynamically loaded from database.";
                pdfForger.generateTrojanPDF(fileName, user.getName(), simulatedCV, actionPlan);

                // 4. Update the Database Card
                appCard.setCoverLetter(coverLetter);
                appCard.setActionPlan(actionPlan);
                appCard.setKanbanStatus("APPLIED");
                applicationDAO.update(appCard);

                IO.println("✅ ARSENAL DEPLOYED: Cover Letter, PDF, and Database successfully updated for " + job.getCompany());
                // 5. Trigger the Skill Gap Analyzer
                IO.println("🧬 Triggering the Skill Gap Analyzer...");
                skillAnalyzer.analyzeAndInjectMissingSkills(user, job);

            } catch (Exception e) {
                IO.println("❌ Arsenal Generation Failed: " + e.getMessage());
            }
        });
    }
}