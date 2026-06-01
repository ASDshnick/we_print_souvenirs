package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderType;

public class AllUserOrdersDTO {

    private Long id;
    private OrderType type;
    private OrderStatus status;
    private int completionPercentage;
    private String date;

    public AllUserOrdersDTO() {
    }

    public AllUserOrdersDTO(Long id, OrderType type, OrderStatus status, int completionPercentage, String date) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.completionPercentage = completionPercentage;
        this.date = date;
    }

    public Long getId() { return id; }
    public OrderType getType() { return type; }
    public OrderStatus getStatus() { return status; }
    public int getCompletionPercentage() { return completionPercentage; }
    public String getDate() { return date; }

    public void setId(Long id) { this.id = id; }
    public void setType(OrderType type) { this.type = type; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }
    public void setDate(String date) { this.date = date; }
}
