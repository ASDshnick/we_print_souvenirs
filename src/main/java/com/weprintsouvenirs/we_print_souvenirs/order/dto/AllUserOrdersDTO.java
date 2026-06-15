package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.PaymentStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;

import java.util.List;

public class AllUserOrdersDTO {

    private Long id;
    private int totalAmount;
    private Status status;
    private PaymentStatus paymentStatus;
    private String date;
    private List<Long> productsIds;

    public AllUserOrdersDTO() {
    }

    public AllUserOrdersDTO(Long id, int totalAmount, Status status, PaymentStatus paymentStatus, String date, List<Long> productsIds) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.date = date;
        this.productsIds = productsIds;
    }

    public Long getId() {
        return id;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public Status getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getDate() {
        return date;
    }

    public List<Long> getProductsIds() {
        return productsIds;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setProductsIds(List<Long> productsIds) {
        this.productsIds = productsIds;
    }
}