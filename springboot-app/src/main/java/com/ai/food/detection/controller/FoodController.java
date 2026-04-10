package com.ai.food.detection.controller;

import com.ai.food.detection.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/food")
@CrossOrigin(origins = "*")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @PostMapping("/detect")
    public ResponseEntity<?> detectFood(@RequestParam("image") MultipartFile file) {
        try {
            // Forward image to Flask
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new MultipartInputStreamFileResource(
                    file.getInputStream(), file.getOriginalFilename()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ResponseEntity<Map> flaskResponse = restTemplate.postForEntity(
                    "http://127.0.0.1:5000/predict",
                    new HttpEntity<>(body, headers),
                    Map.class);

            Map flaskResult = flaskResponse.getBody();
            String food = flaskResult.get("food").toString().toLowerCase().trim();
            Object confidence = flaskResult.get("confidence");

            System.out.println("Flask predicted: " + food + " (" + confidence + "%)");

            // Lookup nutrition details from CSV
            Map<String, String> details = foodService.getFoodDetails(food);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("food", food);
            result.put("confidence", confidence);

            if (details != null) {
                result.put("calories",  details.get("calories"));
                result.put("protein",   details.get("protein"));
                result.put("fat",       details.get("fat"));
                result.put("fiber",     details.get("fiber"));
                result.put("sugar",     details.get("sugar"));
                result.put("health",    details.get("health"));
                result.put("risk",      details.get("risk"));
            } else {
                System.out.println("No CSV match for: " + food);
                result.put("calories", "N/A");
                result.put("protein",  "N/A");
                result.put("fat",      "N/A");
                result.put("fiber",    "N/A");
                result.put("sugar",    "N/A");
                result.put("health",   "No data available");
                result.put("risk",     "No data available");
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
