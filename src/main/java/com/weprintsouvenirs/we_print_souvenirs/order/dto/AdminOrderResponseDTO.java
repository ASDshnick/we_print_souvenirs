package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.DeliveryType;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderType;

import java.time.LocalDateTime;

public class AdminOrderResponseDTO {

    private Long id;
    private Long userId;
    private String username;
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

    public AdminOrderResponseDTO(Long id, Long userId, String username, OrderType type,
                                  String requirements, OrderStatus status, int completionPercentage,
                                  String labels, int revisionCount, DeliveryType deliveryType,
                                  Integer quantity, Boolean colorPrint, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.type = type;
        this.requirements = requirements;
        this.status = status;
        this.completionPercentage = completionPercentage;
        this.labels = labels;
        this.revisionCount = revisionCount;
        this.deliveryType = deliveryType;
        this.quantity = quantity;
        this.colorPrint = colorPrint;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
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
}
