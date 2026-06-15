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
    private List<Long> productIds;

    public AllUserOrdersDTO() {
    }

    public AllUserOrdersDTO(Long id, int totalAmount, Status status, PaymentStatus paymentStatus, String date, List<Long> productIds) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.date = date;
        this.productIds = productIds;
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

    public List<Long> getProductIds() {
        return productIds;
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

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public void setProductsIds(List<Long> productsIds) {
        this.productIds = productsIds;
    }
}
