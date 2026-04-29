package com.trendaura.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * AI model integration - stub endpoints.
 * Frontend → Backend → ML Model (CNN classification, LSTM trend forecasting).
 * Replace with real calls to your Python/ML service when ready.
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    /** CNN outfit classification - stub. Pass image, get predicted category/state/fashion type. */
    @PostMapping("/classify")
    public ResponseEntity<Map<String, Object>> classifyImage(@RequestParam("image") MultipartFile image) {
        Map<String, Object> stub = new HashMap<>();
        stub.put("success", true);
        stub.put("message", "AI classification stub - integrate CNN model here");
        stub.put("predictedCategory", "saree");
        stub.put("predictedState", "tamil_nadu");
        stub.put("confidence", 0.92);
        return ResponseEntity.ok(stub);
    }

    /** LSTM trend forecasting - stub. Returns predicted trending styles. */
    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrendForecast(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String fashionType) {
        Map<String, Object> stub = new HashMap<>();
        stub.put("success", true);
        stub.put("message", "Trend forecast stub - integrate LSTM model here");
        stub.put("trending", new String[] { "saree", "lehenga", "salwar_kameez" });
        return ResponseEntity.ok(stub);
    }
}
