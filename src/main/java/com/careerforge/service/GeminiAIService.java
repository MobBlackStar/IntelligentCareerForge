package com.careerforge.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * // SARAH: This is the OMNI-RESILIENT version of Fedi's Brain.
 * // It includes a "Safety Shield" to catch Google's errors before they crash the app.
 */
public class GeminiAIService {

    private static final String API_KEY = "AIzaSyBmuDE6gE4kMLltQMMFRvc-U3yN27CVxhI";

    /**
     * THE FEDI-SUCCESS CONFIGURATION
     * We use v1beta (Experimental Wing) + gemini-2.0-flash-exp (The real name of the "2.5" model)
     */
    // THE STABLE SUPREMACY PATH: Using v1beta (for feature access) + gemini-1.5-flash (Reliable Speed)
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

    // FEYNMAN COMMENT: This is the "Postman" method.
    // It sends the letter and checks if Google sent a gift or a complaint.
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

            // 🛑 THE SAFETY SHIELD
            JSONObject jsonResponse = new JSONObject(rawResponse);

            // If Google sent an error, tell us what it is!
            if (jsonResponse.has("error")) {
                String errorMsg = jsonResponse.getJSONObject("error").getString("message");
                System.out.println("❌ THE ORACLE REFUSED: " + errorMsg);
                return "ORACLE ERROR: " + errorMsg;
            }

            // If no error, extract the answer normally
            return jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (Exception e) {
            System.out.println("❌ Strike Failed: " + e.getMessage());
            return "ERROR: Connection failed.";
        }
    }
}