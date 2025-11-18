package com.library.Library.Management.System.dto;

public class UserResponse {

    private Long id;
    private String email;
    private String role;
    private boolean blacklisted;

    public UserResponse(Long id, String email, String role, boolean blacklisted) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.blacklisted = blacklisted;
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isBlacklisted() { return blacklisted; }
}
