package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.PaymentStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;

public class AdminOrderUpdateRequestDTO {
    private Status status;
    private PaymentStatus paymentStatus;
    private String adminNote;

    public AdminOrderUpdateRequestDTO() {
    }

    public AdminOrderUpdateRequestDTO(Status status, PaymentStatus paymentStatus, String adminNote) {
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.adminNote = adminNote;
    }

    public Status getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}
