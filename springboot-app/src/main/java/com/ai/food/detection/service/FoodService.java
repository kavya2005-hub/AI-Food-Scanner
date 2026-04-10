package com.ai.food.detection.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class FoodService {

    private final Map<String, Map<String, String>> foodMap = new HashMap<>();

    public FoodService() {
        loadCSV();
    }

    private void loadCSV() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/food_data.csv")))) {

            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] data = parseCsvLine(line);
                if (data.length < 8) continue;

                Map<String, String> food = new LinkedHashMap<>();
                food.put("food",     data[0].trim().toLowerCase());
                food.put("calories", data[1].trim());
                food.put("protein",  data[2].trim());
                food.put("fat",      data[3].trim());
                food.put("fiber",    data[4].trim());
                food.put("sugar",    data[5].trim());
                food.put("health",   data[6].trim());
                food.put("risk",     data[7].trim());

                foodMap.put(data[0].trim().toLowerCase(), food);
            }
            System.out.println("FoodService loaded " + foodMap.size() + " foods from CSV.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') { inQuotes = !inQuotes; }
            else if (c == ',' && !inQuotes) { tokens.add(sb.toString()); sb.setLength(0); }
            else { sb.append(c); }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    public Map<String, String> getFoodDetails(String name) {
        String key = name.trim().toLowerCase();
        // exact match first
        if (foodMap.containsKey(key)) return foodMap.get(key);
        // partial match fallback
        for (String k : foodMap.keySet()) {
            if (key.contains(k) || k.contains(key)) return foodMap.get(k);
        }
        return null;
    }
}
