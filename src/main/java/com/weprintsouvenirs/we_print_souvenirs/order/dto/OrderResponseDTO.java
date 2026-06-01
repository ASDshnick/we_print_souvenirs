package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.DeliveryType;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderType;

import java.time.LocalDateTime;

public class OrderResponseDTO {
    private Long id;
    private OrderType type;
    private String requirements;
    private OrderStatus status;
    private int completionPercentage;
    private String labels;
    private int revisionCount;
    private DeliveryType deliveryType;
    private Integer quantity;
    private Boolean colorPrint;
    private LocalDateTime createdAt;

    public OrderResponseDTO() {
    }

    public Long getId() { return id; }
    public OrderType getType() { return type; }
    public String getRequirements() { return requirements; }
    public OrderStatus getStatus() { return status; }
    public int getCompletionPercentage() { return completionPercentage; }
    public String getLabels() { return labels; }
    public int getRevisionCount() { return revisionCount; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public Integer getQuantity() { return quantity; }
    public Boolean getColorPrint() { return colorPrint; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setType(OrderType type) { this.type = type; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }
    public void setLabels(String labels) { this.labels = labels; }
    public void setRevisionCount(int revisionCount) { this.revisionCount = revisionCount; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setColorPrint(Boolean colorPrint) { this.colorPrint = colorPrint; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
