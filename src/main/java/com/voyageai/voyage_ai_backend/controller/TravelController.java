package com.voyageai.voyage_ai_backend.controller;


import com.voyageai.voyage_ai_backend.dto.TravelPlanRequest;
import com.voyageai.voyage_ai_backend.service.AIService;
import com.voyageai.voyage_ai_backend.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/travel")
public class TravelController {

    private final AIService aiService;

    @Autowired
    private TripService tripService;

    public TravelController(AIService aiService){
        this.aiService = aiService;
    }

    @PostMapping("/plan")
    public ResponseEntity<?> generatePlan(@RequestBody TravelPlanRequest request) {
        String plan = aiService.generateTravelPlan(request);
        return ResponseEntity.ok(Map.of("plan", plan));
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveTrip(@RequestBody Map<String, Object> body, Principal principal) {

        String email = principal.getName();

        Object request = body.get("request");
        String plan = body.get("plan").toString();

        tripService.saveTrip(email, request, plan);

        return ResponseEntity.ok(Map.of("message", "Trip saved successfully"));
    }

    @GetMapping("/my-trips")
    public ResponseEntity<?> getMyTrips(Principal principal) {

        String email = principal.getName();

        return ResponseEntity.ok(tripService.getUserTrips(email));
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String message) {
        return ResponseEntity.ok(aiService.chatWithAI(message));
    }
}
