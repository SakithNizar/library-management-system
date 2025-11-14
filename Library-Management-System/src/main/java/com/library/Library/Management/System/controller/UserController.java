package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.User;
import com.library.Library.Management.System.repository.UserRepository;
import com.library.Library.Management.System.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ============================================================
    // 📌 1. GET ALL USERS — LIBRARIAN ONLY
    // ============================================================
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<List<User>> getAllUsers() {
        return new ApiResponse<>(
                "Fetched all users successfully",
                userRepository.findAll()
        );
    }

    // ============================================================
    // 📌 2. GET USER BY ID — LIBRARIAN + USER CAN VIEW OWN PROFILE
    // ============================================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_LIBRARIAN')")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> new ApiResponse<>("User found", user))
                .orElseGet(() -> new ApiResponse<>("User not found", null));
    }

    // ============================================================
    // 📌 3. CREATE USER
    // ============================================================
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER', 'ROLE_LIBRARIAN')")
    public ApiResponse<User> createUser(@RequestBody User user) {
        User saved = userRepository.save(user);
        return new ApiResponse<>("User created successfully!", saved);
    }

    // ============================================================
    // 📌 4. UPDATE USER PROFILE — USER + LIBRARIAN
    // ============================================================
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_LIBRARIAN')")
    public ApiResponse<User> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser
    ) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(updatedUser.getEmail());
                    user.setPassword(updatedUser.getPassword());
                    user.setRole(updatedUser.getRole());
                    userRepository.save(user);
                    return new ApiResponse<>("User updated successfully!", user);
                })
                .orElseGet(() -> new ApiResponse<>("User not found!", null));
    }

    // ============================================================
    // 📌 5. DELETE USER — USER + LIBRARIAN
    // ============================================================
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_LIBRARIAN')")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ApiResponse<>("User not found!", null);
        }
        userRepository.deleteById(id);
        return new ApiResponse<>("User deleted successfully!", "ID: " + id);
    }

    // ============================================================
    // 📌 6. BLACKLIST USER — LIBRARIAN ONLY
    // ============================================================
    @PutMapping("/blacklist/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<String> blacklistUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setBlacklisted(true);
                    userRepository.save(user);
                    return new ApiResponse<>("User blacklisted successfully!", "ID: " + id);
                })
                .orElseGet(() -> new ApiResponse<>("User not found!", null));
    }
}
