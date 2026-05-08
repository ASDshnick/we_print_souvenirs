package com.weprintsouvenirs.we_print_souvenirs.user.dto;

public class ChangePasswordRequestDTO {
    private String oldPassword;
    private String newPassword;

    public ChangePasswordRequestDTO() {
    }

    public ChangePasswordRequestDTO(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
