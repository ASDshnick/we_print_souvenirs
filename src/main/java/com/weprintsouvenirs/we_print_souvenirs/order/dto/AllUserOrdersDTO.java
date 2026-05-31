package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;

public class AllUserOrdersDTO {

    private Long id;
    private int totalAmount;
    private Status status;
    private String date;

    public AllUserOrdersDTO() {
    }

    public AllUserOrdersDTO(Long id, int totalAmount, Status status, String date) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.date = date;
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

    public String getDate() {
        return date;
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

    public void setDate(String date) {
        this.date = date;
    }
}
