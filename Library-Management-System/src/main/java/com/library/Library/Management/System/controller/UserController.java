package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.dto.UserResponse;
import com.library.Library.Management.System.model.User;
import com.library.Library.Management.System.repository.UserRepository;
import com.library.Library.Management.System.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;
import com.library.Library.Management.System.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getRole().name(),
                u.isBlacklisted()
        );
    }

    // 1. GET ALL USERS — LIBRARIAN
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> list = userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>("Fetched all users successfully", list);
    }

    // 2. GET USER BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> new ApiResponse<>("User found", toResponse(user)))
                .orElseGet(() -> new ApiResponse<>("User not found", null));
    }

    // 3. CREATE USER — LIBRARIAN ONLY
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<UserResponse> createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return new ApiResponse<>("User created successfully", toResponse(saved));
    }

    // 4. UPDATE USER
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> updatedUser
    ) {
        return userRepository.findById(id)
                .map(user -> {

                    if (updatedUser.containsKey("email")) {
                        user.setEmail(updatedUser.get("email"));
                    }

                    if (updatedUser.containsKey("password")) {
                        // ✅ FIXED — encode password
                        user.setPassword(passwordEncoder.encode(updatedUser.get("password")));
                    }

                    if (updatedUser.containsKey("role")) {
                        user.setRole(Role.valueOf(updatedUser.get("role")));
                    }

                    userRepository.save(user);
                    return new ApiResponse<>("User updated successfully!", toResponse(user));
                })
                .orElseGet(() -> new ApiResponse<>("User not found!", null));
    }

    // 5. DELETE USER
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ApiResponse<>("User not found", null);
        }
        userRepository.deleteById(id);
        return new ApiResponse<>("User deleted successfully", "Deleted ID: " + id);
    }

    // ⭐ GET LOGGED-IN USER — USER + LIBRARIAN
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_LIBRARIAN')")
    public ApiResponse<UserResponse> getMyDetails(Authentication auth) {
        String email = auth.getName();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ApiResponse<>("User not found!", null);
        }

        return new ApiResponse<>("Fetched logged-in user details successfully!",
                new UserResponse(user.getId(), user.getEmail(), user.getRole().name(), user.isBlacklisted()));
    }

    // 6. BLACKLIST USER — LIBRARIAN
    @PutMapping("/blacklist/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<UserResponse> blacklistUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setBlacklisted(true);
                    userRepository.save(user);
                    return new ApiResponse<>("User blacklisted successfully!", toResponse(user));
                })
                .orElseGet(() -> new ApiResponse<>("User not found!", null));
    }

    // 7. UN-BLACKLIST USER — LIBRARIAN ONLY
    @PutMapping("/unblacklist/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<UserResponse> unBlacklistUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setBlacklisted(false);
                    userRepository.save(user);
                    return new ApiResponse<>("User un-blacklisted successfully!", toResponse(user));
                })
                .orElseGet(() -> new ApiResponse<>("User not found!", null));
    }


}
