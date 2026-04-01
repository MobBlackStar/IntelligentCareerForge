package com.careerforge.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * // SARAH: The Unified Brain.
 * // I have fixed the "Missing Return" and the "Red IO" errors.
 */
public class GeminiAIService {

    private static final String API_KEY = "AIzaSyAy0pPt2BA5BqR4ebKWzibwZBnQF6RNm2A";
    // THE ULTIMATE BULLETPROOF URL (Back to v1beta for AI Studio Keys)
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    private HttpClient httpClient;

    public GeminiAIService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String interrogateCV(String rawUserText) {
        String systemPrompt = """
            SYSTEM ROLE: You are an Elite Ex-Recruiter for Big Tech.
            YOUR PRIME DIRECTIVE: Analyze the user's data. 
            Critique it brutally in 2 sentences. 
            Then, ask 2 probing questions to uncover quantifiable metrics.
            """;
        return callGemini(systemPrompt + "\\n\\nINPUT DATA:\\n" + rawUserText);
    }

    public String extractJobDetails(String rawText) {
        String prompt = """
            You are an elite data extractor. Return ONLY a JSON object:
            { "title": "...", "company": "...", "pain_point": "..." }
            RAW JOB DESCRIPTION:
            """ + rawText;
        return callGemini(prompt);
    }

    private String callGemini(String promptText) {
        try {
            JSONObject part = new JSONObject().put("text", promptText);
            JSONArray parts = new JSONArray().put(part);
            JSONObject content = new JSONObject().put("parts", parts);
            JSONArray contents = new JSONArray().put(content);
            JSONObject requestBody = new JSONObject().put("contents", contents);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String rawResponse = response.body();
            JSONObject jsonResponse = new JSONObject(rawResponse);

            // Shield 1: Google sends an error
            if (jsonResponse.has("error")) {
                String msg = jsonResponse.getJSONObject("error").getString("message");
                System.out.println("❌ Google API Refused: " + msg);
                return "ERROR: " + msg;
            }

            // Shield 2: Safety block
            if (!jsonResponse.has("candidates")) {
                return "ERROR: Response blocked by safety filters.";
            }

            // Success path
            String aiAnswer = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // FEYNMAN COMMENT: We MUST return the answer here!
            return aiAnswer;

        } catch (Exception e) {
            // SARAH: We use standard System.out to avoid the "Forbidden Import"
            System.out.println("❌ Strike Failed: " + e.getMessage());
            return "ERROR: Connection failed.";
        }
    }
}