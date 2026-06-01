package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;

public class AdminOrderUpdateDTO {

    private OrderStatus status;
    private Integer completionPercentage;
    private String labels;

    public OrderStatus getStatus() { return status; }
    public Integer getCompletionPercentage() { return completionPercentage; }
    public String getLabels() { return labels; }

    public void setStatus(OrderStatus status) { this.status = status; }
    public void setCompletionPercentage(Integer completionPercentage) { this.completionPercentage = completionPercentage; }
    public void setLabels(String labels) { this.labels = labels; }
}
