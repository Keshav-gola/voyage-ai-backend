package com.voyageai.voyage_ai_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voyageai.voyage_ai_backend.dto.TravelPlanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ================================
    // 🌍 PUBLIC METHODS
    // ================================

    // ✅ Travel Plan Generator
    public String generateTravelPlan(TravelPlanRequest request) {
        long days = calculateDays(request.getStartDate(), request.getEndDate());
        String prompt = buildTravelPrompt(request, days);
        return callGemini(prompt);
    }

    // ✅ Simple Chatbot (no memory)
    public String chatWithAI(String userMessage) {
        String prompt = """
                You are a smart and friendly travel assistant.
                
                Answer the user's question in a helpful, clear, and conversational way.
                
                User: %s
                """.formatted(userMessage);

        return callGemini(prompt);
    }

    // ✅ Chatbot with conversation memory (ADVANCED - optional)
    public String chatWithMemory(List<Message> messages) {
        String prompt = buildChatPrompt(messages);
        return callGemini(prompt);
    }

    // ================================
    // 🧠 CORE GEMINI CALL (REUSABLE)
    // ================================

    private String callGemini(String prompt) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=" + apiKey;

        String body = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": "%s" }
                      ]
                    }
                  ]
                }
                """.formatted(prompt.replace("\"", "\\\""));

        try {
            String response = restTemplate.postForObject(url, body, String.class);
            return extractText(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating AI response";
        }
    }

    // ================================
    // 🧠 PROMPT BUILDERS
    // ================================

    // Travel Plan Prompt
    private String buildTravelPrompt(TravelPlanRequest req, long days) {
        return String.format("""
                        Generate a detailed travel itinerary.
                        
                        Trip Details:
                        - From: %s
                        - To: %s
                        - Travel Mode: %s
                        - Budget: %s
                        - Duration: %d days
                        - Travellers: %d
                        
                        Instructions:
                        - Create day-wise plan
                        - Include places, activities, and timings
                        - Suggest transport based on travel mode
                        - Add hotel suggestions
                        - Add budget tips
                        - Add travel tips
                        
                        Format rules:
                        - Use emojis for sections (Day, Budget, Tips, Stay)
                        - Keep spacing clean
                        - Avoid long paragraphs
                        - Make it easy to read for mobile users
                        """,
                req.getSource(),
                req.getDestination(),
                req.getTravelMode(),
                req.getBudget(),
                days,
                req.getTravellers()
        );
    }

    // Chat Prompt with Memory
    private String buildChatPrompt(List<Message> messages) {
        StringBuilder prompt = new StringBuilder(
                "You are a helpful travel assistant.\n" +
                        "Format responses in markdown using headings, bullet points, and proper spacing.\n\n"
        );

        for (Message msg : messages) {
            prompt.append(msg.getRole())
                    .append(": ")
                    .append(msg.getContent())
                    .append("\n");
        }

        return prompt.toString();
    }

    // ================================
    // 📅 DATE CALCULATION
    // ================================

    private long calculateDays(String start, String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    // ================================
    // 🔍 RESPONSE PARSER
    // ================================

    private String extractText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            return root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing AI response";
        }
    }

    // ================================
    // 📦 INNER DTO FOR CHAT MEMORY
    // ================================

    public static class Message {
        private String role;     // "user" or "assistant"
        private String content;

        public Message() {
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}