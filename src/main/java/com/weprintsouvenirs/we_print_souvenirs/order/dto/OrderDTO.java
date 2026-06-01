package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.DeliveryType;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderType;

/** DTO для создания заказа (FR-06 — FR-09). */
public class OrderDTO {

    private OrderType type;
    private String requirements;
    private Integer polygons;
    private boolean polygonsImportant;
    private boolean deadlineImportant;
    private String deadline;

    // Только для PRINT_3D (FR-08)
    private DeliveryType deliveryType;
    private Integer quantity;
    private Boolean colorPrint;

    public OrderType getType() { return type; }
    public String getRequirements() { return requirements; }
    public Integer getPolygons() { return polygons; }
    public boolean isPolygonsImportant() { return polygonsImportant; }
    public boolean isDeadlineImportant() { return deadlineImportant; }
    public String getDeadline() { return deadline; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public Integer getQuantity() { return quantity; }
    public Boolean getColorPrint() { return colorPrint; }

    public void setType(OrderType type) { this.type = type; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public void setPolygons(Integer polygons) { this.polygons = polygons; }
    public void setPolygonsImportant(boolean polygonsImportant) { this.polygonsImportant = polygonsImportant; }
    public void setDeadlineImportant(boolean deadlineImportant) { this.deadlineImportant = deadlineImportant; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setColorPrint(Boolean colorPrint) { this.colorPrint = colorPrint; }
}
