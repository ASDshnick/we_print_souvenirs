package com.weprintsouvenirs.we_print_souvenirs.user.dto;

public class ChangeUserDataRequestDTO {
    private String username;
    private String email;
    private String phone;

    public ChangeUserDataRequestDTO() {
    }

    public ChangeUserDataRequestDTO(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
