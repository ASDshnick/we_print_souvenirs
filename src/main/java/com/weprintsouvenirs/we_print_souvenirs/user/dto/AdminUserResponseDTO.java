package com.weprintsouvenirs.we_print_souvenirs.user.dto;

import java.time.LocalDateTime;

public class AdminUserResponseDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String adminNote;
    private LocalDateTime registeredAt;
    private LocalDateTime lastLoginAt;

    public AdminUserResponseDTO() {
    }

    public AdminUserResponseDTO(Long id, String name, String username, String email, String phone, String adminNote, LocalDateTime registeredAt, LocalDateTime lastLoginAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.adminNote = adminNote;
        this.registeredAt = registeredAt;
        this.lastLoginAt = lastLoginAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}