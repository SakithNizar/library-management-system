package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.User;
import com.library.Library.Management.System.model.Role;
import com.library.Library.Management.System.repository.UserRepository;
import com.library.Library.Management.System.security.JwtService;
import com.library.Library.Management.System.service.EmailService;   // <-- Added

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;   // <-- Added


    // ------------------------------------------------------
    // SIGNUP
    // ------------------------------------------------------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        if (userRepository.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "❌ Email already exists!"));
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf(role));   // USER or LIBRARIAN
        user.setBlacklisted(false);

        userRepository.save(user);

        // ------------------------------------------------------
        // SEND EMAIL CONFIRMATION
        // ------------------------------------------------------
        emailService.sendSimpleMessage(
                user.getEmail(),
                "Welcome to Library Management System",
                "Hello " + user.getEmail() + ",\n\nYour account has been created successfully. Welcome!"
        );

        return ResponseEntity.ok(Map.of("message", "✅ User registered successfully!"));
    }


    // ------------------------------------------------------
    // LOGIN
    // ------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        User user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "❌ Invalid email or password"));
        }

        // Block blacklisted users
        if (user.isBlacklisted()) {
            return ResponseEntity.status(403).body(
                    Map.of("message", "⛔ Your account is blacklisted. Contact librarian.")
            );
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ Login successful!");
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
