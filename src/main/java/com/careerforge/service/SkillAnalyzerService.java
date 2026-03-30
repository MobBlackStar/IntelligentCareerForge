package com.careerforge.service;

import com.careerforge.dao.SkillDAO;
import com.careerforge.model.JobOffer;
import com.careerforge.model.Skill;
import com.careerforge.model.User;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * // FEDI: The Skill Heist Engine.
 * // This fulfills the "Suggestions de compétences manquantes" requirement.
 */
public class SkillAnalyzerService {

    private GeminiAIService oracle = new GeminiAIService();
    private SkillDAO skillDAO = new SkillDAO();

    public void analyzeAndInjectMissingSkills(User user, JobOffer job) {
        IO.println("🧬 SKILL ANALYZER: Scanning " + user.getName() + " against " + job.getCompany() + " requirements...");

        // 1. Fetch the user's current skills from the database
        ArrayList<Skill> userSkills = skillDAO.getSkillsByUserId(user.getId());
        List<String> skillNames = new ArrayList<>();
        for (Skill s : userSkills) {
            skillNames.add(s.getSkillName());
        }
        String currentSkillsStr = String.join(", ", skillNames);

        if (currentSkillsStr.isEmpty()) {
            currentSkillsStr = "None recorded yet.";
        }

        // 2. Build the highly specific JSON-enforced Prompt
        String prompt = """
            You are an elite ATS System Analyzer.
            The candidate has the following skills: [%s]
            The target job description is: "%s"
            
            Identify exactly 3 critical hard skills the candidate is MISSING for this job.
            You MUST return ONLY a valid JSON array of strings. Do not use markdown. Do not explain.
            Example format:["Docker", "Kubernetes", "GraphQL"]
            """.formatted(currentSkillsStr, job.getRawDescription());

        try {
            // 3. Call the Oracle
            IO.println("🧠 Asking Oracle for Missing Skills...");
            String aiJsonResponse = oracle.interrogateCV(prompt);

            if (aiJsonResponse.startsWith("ERROR")) {
                throw new Exception(aiJsonResponse);
            }

            // Clean the JSON string just in case Gemini adds markdown ticks
            String cleanJson = aiJsonResponse.replace("```json", "").replace("```", "").trim();

            // 4. Parse the JSON Array
            JSONArray missingSkillsArray = new JSONArray(cleanJson);

            // 5. Inject the missing skills into the database as Red Nodes (isAcquired = false)
            for (int i = 0; i < missingSkillsArray.length(); i++) {
                String missingSkillName = missingSkillsArray.getString(i);

                // Ensure we don't add a skill they already have!
                if (!skillNames.contains(missingSkillName)) {
                    Skill missingSkill = new Skill(user.getId(), missingSkillName, false);
                    skillDAO.create(missingSkill);
                    IO.println("❌ Missing Skill Identified & Saved: " + missingSkillName);
                }
            }

            IO.println("✅ SKILL HEIST COMPLETE. The RPG Skill Tree is updated.");

        } catch (Exception e) {
            IO.println("❌ Skill Analyzer Failed: " + e.getMessage());
        }
    }
}