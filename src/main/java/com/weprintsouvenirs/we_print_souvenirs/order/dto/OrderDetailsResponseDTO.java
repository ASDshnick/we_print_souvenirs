package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Payment;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.PaymentStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailsResponseDTO {
    private Long id;
    private String customerUsername;
    private String customerEmail;
    private int totalAmount;
    private Status status;
    private Payment paymentMethod;
    private PaymentStatus paymentStatus;
    private String adminNote;
    private LocalDateTime createdAt;
    private OrderUserResponseDTO user;
    private List<OrderItemResponseDTO> items;

    public OrderDetailsResponseDTO() {
    }

    public OrderDetailsResponseDTO(Long id, String customerUsername, String customerEmail, int totalAmount, Status status, Payment paymentMethod, PaymentStatus paymentStatus, String adminNote, LocalDateTime createdAt, OrderUserResponseDTO user, List<OrderItemResponseDTO> items) {
        this.id = id;
        this.customerUsername = customerUsername;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.adminNote = adminNote;
        this.createdAt = createdAt;
        this.user = user;
        this.items = items;
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

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderUserResponseDTO getUser() {
        return user;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
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

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(OrderUserResponseDTO user) {
        this.user = user;
    }

    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }
}
