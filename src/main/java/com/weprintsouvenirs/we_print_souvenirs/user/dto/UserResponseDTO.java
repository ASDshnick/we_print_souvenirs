package com.weprintsouvenirs.we_print_souvenirs.user.dto;

public class UserResponseDTO {
    private String name;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

}
