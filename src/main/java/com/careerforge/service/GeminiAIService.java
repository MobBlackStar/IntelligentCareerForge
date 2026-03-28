package com.careerforge.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiAIService {

    // 🛑 FEDI: PASTE YOUR ACTUAL API KEY HERE
    private static final String API_KEY = "AIzaSyAy0pPt2BA5BqR4ebKWzibwZBnQF6RNm2A";

    // Upgraded to 2.5-flash as per Architect's command
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private HttpClient httpClient;

    public GeminiAIService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String interrogateCV(String rawUserText) {
        String systemPrompt = """
            SYSTEM ROLE: You are an Elite Ex-Recruiter for Big Tech.
            YOUR PRIME DIRECTIVE: Analyze the user's raw CV data. 
            Critique it brutally but professionally in 2 sentences. 
            Then, ask 2 to 3 highly specific, probing questions to uncover quantifiable metrics (The XYZ formula).
            Output ONLY the critique and the questions. Do not format the CV yet.
            """;
        String fullPrompt = systemPrompt + "\n\nUSER RAW CV:\n" + rawUserText;
        return callGemini(fullPrompt);
    }

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

    public String extractJobDetails(String rawText) {
        String prompt = """
            You are an elite data extractor. Read the following messy job description.
            Extract the Job Title, the Company Name, and infer a 1-sentence 'Company Pain Point' (why they are hiring).
            You MUST return ONLY a valid JSON object in this exact format, nothing else (no markdown, no backticks):
            {
              "title": "Data Scientist",
              "company": "Google",
              "pain_point": "They are struggling to analyze large datasets efficiently."
            }
            
            RAW JOB DESCRIPTION:
            """ + rawText;
        return callGemini(prompt);
    }

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

            // Shield 1: Google sends an explicit error
            if (jsonResponse.has("error")) {
                String googleError = jsonResponse.getJSONObject("error").getString("message");
                IO.println("❌ Google API Refused: " + googleError);
                return "ERROR: " + googleError;
            }

            // Shield 2: Google Safety Filters blocked the prompt
            if (!jsonResponse.has("candidates")) {
                IO.println("❌ Gemini blocked the response! RAW GOOGLE OUTPUT: " + rawResponse);
                return "ERROR: Blocked by Google or Invalid Format.";
            }

            // Success path
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