package com.weprintsouvenirs.we_print_souvenirs.user.dto;

import com.weprintsouvenirs.we_print_souvenirs.user.Role;

public class UserRegisterDTO {
    String username;
    String password;
    String email;
    Role role;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
