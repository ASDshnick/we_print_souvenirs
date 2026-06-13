package com.weprintsouvenirs.we_print_souvenirs.order.model;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Payment;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.PaymentStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "customer_username")
    private String customerUsername;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "total_amount")
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private Payment paymentMethod = Payment.CARD;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public OrderEntity() {
    }

    public OrderEntity(Long id, UserEntity user, String customerUsername, String customerEmail, int totalAmount, Status status, Payment paymentMethod, LocalDateTime createdAt, PaymentStatus paymentStatus) {
        this.id = id;
        this.user = user;
        this.customerUsername = customerUsername;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
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

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
