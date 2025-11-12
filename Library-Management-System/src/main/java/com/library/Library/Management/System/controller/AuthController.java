package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.dto.SignupRequest;
import com.library.Library.Management.System.model.User;
import com.library.Library.Management.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            return "❌ Email already exists!";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);

        return "✅ User registered successfully!";
    }
}
