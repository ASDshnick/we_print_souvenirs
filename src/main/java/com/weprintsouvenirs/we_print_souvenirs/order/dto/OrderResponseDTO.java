package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Payment;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;

import java.time.LocalDateTime;

/**
 * Возвращаемое DTO после создания заказа
 */
public class OrderResponseDTO {
    private Long id;
    private String customerUsername;
    private String customerEmail;
    private int totalAmount;
    private Status status;
    private Payment paymentMethod;
    private LocalDateTime createdAt;

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(Long id, String customerUsername, String customerEmail, int totalAmount, Status status, Payment paymentMethod, LocalDateTime createdAt) {
        this.id = id;
        this.customerUsername = customerUsername;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public Status getStatus() {
        return status;
    }

    public Payment getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPaymentMethod(Payment paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
