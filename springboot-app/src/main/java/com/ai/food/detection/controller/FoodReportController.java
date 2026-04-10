package com.ai.food.detection.controller;

import com.ai.food.detection.model.FoodReport;
import com.ai.food.detection.repository.FoodReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/food-report")
@CrossOrigin(origins = "*")
public class FoodReportController {

    @Autowired
    private FoodReportRepository repo;

    /** Return all rows from food_report table */
    @GetMapping("/all")
    public ResponseEntity<List<FoodReport>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    /**
     * GET lookup — used by report.html
     * GET /food-report/lookup?food=apple
     */
    @GetMapping("/lookup")
    public ResponseEntity<?> lookup(@RequestParam String food) {
        Optional<FoodReport> result = repo.findByFoodIgnoreCase(food.trim());
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.status(404)
                .body(Map.of("status", "not_found", "message", "Food not found in database"));
    }

    /**
     * POST lookup — alternative JSON body style
     * POST /food-report/lookup  { "food": "apple" }
     */
    @PostMapping("/lookup")
    public ResponseEntity<?> lookupPost(@RequestBody Map<String, String> body) {
        String food = body.getOrDefault("food", "").trim();
        if (food.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Food name is required"));
        }
        Optional<FoodReport> result = repo.findByFoodIgnoreCase(food);
        if (result.isPresent()) {
            return ResponseEntity.ok(Map.of("status", "found", "data", result.get()));
        }
        return ResponseEntity.status(404)
                .body(Map.of("status", "not_found", "message", "Food not found in database"));
    }
}
