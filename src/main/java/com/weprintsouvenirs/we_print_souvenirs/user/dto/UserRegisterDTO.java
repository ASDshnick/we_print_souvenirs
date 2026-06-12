package com.weprintsouvenirs.we_print_souvenirs.user.dto;

import com.weprintsouvenirs.we_print_souvenirs.user.Role;

public class UserRegisterDTO {
    String name;
    String username;
    String password;
    Role role;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
