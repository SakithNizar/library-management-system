package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.User;
import com.library.Library.Management.System.model.Role;
import com.library.Library.Management.System.repository.UserRepository;
import com.library.Library.Management.System.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        if (userRepository.findByEmail(request.get("email")) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "❌ Email already exists!"));
        }

        User user = new User();
        user.setEmail(request.get("email"));
        user.setPassword(passwordEncoder.encode(request.get("password")));
        user.setRole(Role.valueOf(request.get("role"))); // USER or LIBRARIAN
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "✅ User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email"));

        if (user == null || !passwordEncoder.matches(request.get("password"), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "❌ Invalid email or password"));
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
                "message", "✅ Login successful!",
                "token", token,
                "role", user.getRole().name()
        ));
    }
}
