package com.weprintsouvenirs.we_print_souvenirs.user.dto;

import com.weprintsouvenirs.we_print_souvenirs.user.Role;

import java.time.LocalDateTime;

public class AdminUserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String adminNote;
    private LocalDateTime registeredAt;
    private LocalDateTime lastLoginAt;

    public AdminUserResponseDTO() {
    }

    public AdminUserResponseDTO(Long id, String name, String email, String adminNote, LocalDateTime registeredAt, LocalDateTime lastLoginAt) {
        this.id = id;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
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

    public void setEmail(String email) {
        this.email = email;
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
