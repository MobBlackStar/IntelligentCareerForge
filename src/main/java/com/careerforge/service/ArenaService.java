package com.careerforge.service;

import com.careerforge.model.JobOffer;
import com.careerforge.model.User;

/**
 * // FEDI: The Backend Engine for Chamber 4 (The Arena).
 * // This service summons the CEO AI to interrogate the candidate.
 */
public class ArenaService {

    private GeminiAIService oracle = new GeminiAIService();

    /**
     * THE AMBUSH: Generates a highly specific, brutal interview question.
     */
    public String generateBrutalQuestion(User user, JobOffer job) {
        IO.println("⚔️ ARENA SERVICE: Generating ambush question for " + job.getCompany() + "...");

        // FEDI-STANDARD: We use %s as placeholders and .formatted() to inject variables cleanly!
        String prompt = """
            You are the ruthless, elite CEO of %s. 
            You are interviewing %s for the position of %s.
            The company's biggest current pain point is: %s.
            
            Based on this, ask ONE brutally difficult, highly specific interview question. 
            Do not say hello. Do not introduce yourself. Just ask the question. 
            Make it a scenario-based question (e.g., "Our servers just crashed. What is your first command?").
            """.formatted(job.getCompany(), user.getName(), job.getTitle(), job.getCompanyPainPoint());

        String question = oracle.interrogateCV(prompt);

        IO.println("🎯 ARENA QUESTION READY.");
        return question;
    }

    /**
     * THE JUDGMENT: Grades the user's 60-second panic answer.
     */
    public String evaluateAnswer(JobOffer job, String question, String userAnswer) {
        IO.println("⚖️ ARENA SERVICE: The Oracle is judging the candidate's answer...");

        String prompt = """
            You are the CEO of %s.
            You asked this interview question: "%s"
            The candidate answered: "%s"
            
            Grade their answer brutally. Output your response in exactly two lines:
            Line 1: SCORE: [0 to 100]/100
            Line 2:[One sentence explaining exactly why they passed or failed.]
            """.formatted(job.getCompany(), question, userAnswer);

        String judgment = oracle.interrogateCV(prompt);

        IO.println("✅ ARENA JUDGMENT COMPLETE.");
        return judgment;
    }
}