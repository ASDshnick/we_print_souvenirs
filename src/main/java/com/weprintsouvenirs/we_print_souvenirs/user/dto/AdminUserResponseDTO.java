package com.weprintsouvenirs.we_print_souvenirs.user.dto;

import com.weprintsouvenirs.we_print_souvenirs.user.Role;

import java.time.LocalDateTime;

public class AdminUserResponseDTO {

    private long id;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private boolean isBlocked;
    private LocalDateTime createdAt;

    public AdminUserResponseDTO(long id, String username, String email, String phone,
                                Role role, boolean isBlocked, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.isBlocked = isBlocked;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public boolean isBlocked() { return isBlocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
