package com.weprintsouvenirs.we_print_souvenirs.order.model;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Payment;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import jakarta.persistence.*;

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
    private String customer_username;

    @Column(name = "customer_email")
    private String customer_email;

    @Column(name = "total_amount")
    private int total_amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private Payment payment_method;

    public OrderEntity() {
    }

    public OrderEntity(Long id, UserEntity user, String customer_username, String customer_email, int total_amount, Status status, Payment payment_method) {
        this.id = id;
        this.user = user;
        this.customer_username = customer_username;
        this.customer_email = customer_email;
        this.total_amount = total_amount;
        this.status = status;
        this.payment_method = payment_method;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getCustomer_username() {
        return customer_username;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public Status getStatus() {
        return status;
    }

    public Payment getPayment_method() {
        return payment_method;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setCustomer_username(String customer_username) {
        this.customer_username = customer_username;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPayment_method(Payment payment_method) {
        this.payment_method = payment_method;
    }
}
