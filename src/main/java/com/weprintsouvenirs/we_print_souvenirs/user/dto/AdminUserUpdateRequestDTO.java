package com.weprintsouvenirs.we_print_souvenirs.user.dto;

import com.weprintsouvenirs.we_print_souvenirs.user.Role;

public class AdminUserUpdateRequestDTO {
    private String name;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private String adminNote;

    public AdminUserUpdateRequestDTO() {
    }

    public AdminUserUpdateRequestDTO(String name, String username, String email, String phone, Role role, String adminNote) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.adminNote = adminNote;
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

    public Role getRole() {
        return role;
    }

    public String getAdminNote() {
        return adminNote;
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

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}
