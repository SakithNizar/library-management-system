package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.User;
import com.library.Library.Management.System.model.Role;
import com.library.Library.Management.System.repository.UserRepository;
import com.library.Library.Management.System.security.JwtService;
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

        userRepository.save(user);

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

        // Generate JWT using email + role
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ Login successful!");
        response.put("userId", user.getId());       // 👈 ADDED
        response.put("email", user.getEmail());     // optional
        response.put("role", user.getRole().name());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
