package com.careerforge.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * // FEDI: The absolute brain of the Chimera.
 * // This service talks directly to Google Gemini using your Supreme Prompt.
 */
public class GeminiAIService {

    // 🛑 FEDI: Put your actual Gemini API Key here from Google AI Studio!
    private static final String API_KEY = "AIzaSyBmuDE6gE4kMLltQMMFRvc-U3yN27CVxhI";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private HttpClient httpClient;

    public GeminiAIService() {
        // FEYNMAN COMMENT: The HttpClient is our digital postman.
        // We set it up once here so it's ready to deliver our letters (prompts) to Google.
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * THE SUPREME INTERROGATOR
     * Takes the user's raw CV text and asks the Elite Recruiter to analyze it.
     */
    public String interrogateCV(String rawUserText) {

        // FEDI-STANDARD: Injecting your God-Tier Prompt
        String systemPrompt = """
            SYSTEM ROLE: You are an Elite Ex-Recruiter for Big Tech.
            YOUR PRIME DIRECTIVE: Analyze the user's raw CV data. 
            Critique it brutally but professionally in 2 sentences. 
            Then, ask 2 to 3 highly specific, probing questions to uncover quantifiable metrics (The XYZ formula).
            Output ONLY the critique and the questions. Do not format the CV yet.
            """;

        String fullPrompt = systemPrompt + "\\n\\nUSER RAW CV:\\n" + rawUserText;

        return callGemini(fullPrompt);
    }

    /**
     * THE PHANTOM GENERATOR
     * Takes a Job Offer description and generates 3 Ghost Competitors.
     */
    public String generateGhostCVs(String jobDescription) {
        String phantomPrompt = """
            You are an ATS Sandbox Simulator. Read the following Job Description.
            Generate 3 fake candidate profiles (Ghosts) applying for this job in Tunisia.
            Return the result STRICTLY as a JSON array with these keys:[ { "name": "...", "ats_score": 85, "strengths": "...", "weaknesses": "..." } ]
            Do not output markdown, ONLY valid JSON.
            
            JOB DESCRIPTION: 
            """ + jobDescription;

        return callGemini(phantomPrompt);
    }

    /**
     * THE MASTER FORGER (Fedi's Jobby McJobface PDF Export Format)
     */
    public String forgeFinalCV(String interrogationHistory) {
        String forgePrompt = """
            You are the Elite Ex-Recruiter. Based on our interrogation history, output the FINAL, mathematically perfect CV.
            You MUST output exactly in the following Markdown format (Jobby McJobface style). No conversational filler.
            
            ## WORK EXPERIENCE
            **[Company]** | [Location]
            * **[Core Achievement]:** [Power Verb] [Task] resulting in [Metric].
            
            INTERROGATION DATA:
            """ + interrogationHistory;

        return callGemini(forgePrompt);
    }

    /*
     * FEYNMAN COMMENT: The Engine Room.
     * All 3 methods above just build the String. THIS method actually sends the HTTP request.
     * We keep it private so the UI controllers can't mess with the raw HTTP logic. (Encapsulation!)
     */
    private String callGemini(String promptText) {
        try {
            // 1. Build the JSON payload expected by Google Gemini
            JSONObject part = new JSONObject().put("text", promptText);
            JSONArray parts = new JSONArray().put(part);
            JSONObject content = new JSONObject().put("parts", parts);
            JSONArray contents = new JSONArray().put(content);
            JSONObject requestBody = new JSONObject().put("contents", contents);

            // 2. Create the HTTP POST Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            // 3. Send it and wait for the response
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Parse the response JSON to get just the text
            JSONObject jsonResponse = new JSONObject(response.body());

            // Navigate the Gemini JSON tree: candidates[0] -> content -> parts[0] -> text
            String aiAnswer = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            return aiAnswer;

        } catch (Exception e) {
            IO.println("❌ Gemini API Strike Failed: " + e.getMessage());
            return "ERROR: Connection to the Oracle failed.";
        }
    }
}