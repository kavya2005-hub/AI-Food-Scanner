package com.ai.food.detection.controller;

import com.ai.food.detection.model.User;
import com.ai.food.detection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService service;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        return ResponseEntity.ok(service.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String password = data.get("password");

        return ResponseEntity.ok(service.login(email, password));
    }
}
