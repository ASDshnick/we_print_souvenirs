package com.weprintsouvenirs.we_print_souvenirs.order.model;

import com.weprintsouvenirs.we_print_souvenirs.order.DeliveryType;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderType;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OrderType type;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "polygons")
    private Integer polygons;

    @Column(name = "polygons_important")
    private boolean polygonsImportant = false;

    @Column(name = "deadline_important")
    private boolean deadlineImportant = false;

    @Column(name = "deadline")
    private String deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @Column(name = "completion_percentage", nullable = false)
    private int completionPercentage = 0;

    @Column(name = "labels")
    private String labels;

    @Column(name = "revision_count", nullable = false)
    private int revisionCount = 0;

    // Только для PRINT_3D
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private DeliveryType deliveryType;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "color_print")
    private Boolean colorPrint;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public OrderEntity() {
    }

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.NEW;
        }
    }

    public Long getId() { return id; }
    public UserEntity getUser() { return user; }
    public OrderType getType() { return type; }
    public String getRequirements() { return requirements; }
    public Integer getPolygons() { return polygons; }
    public boolean isPolygonsImportant() { return polygonsImportant; }
    public boolean isDeadlineImportant() { return deadlineImportant; }
    public String getDeadline() { return deadline; }
    public OrderStatus getStatus() { return status; }
    public int getCompletionPercentage() { return completionPercentage; }
    public String getLabels() { return labels; }
    public int getRevisionCount() { return revisionCount; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public Integer getQuantity() { return quantity; }
    public Boolean getColorPrint() { return colorPrint; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(UserEntity user) { this.user = user; }
    public void setType(OrderType type) { this.type = type; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public void setPolygons(Integer polygons) { this.polygons = polygons; }
    public void setPolygonsImportant(boolean polygonsImportant) { this.polygonsImportant = polygonsImportant; }
    public void setDeadlineImportant(boolean deadlineImportant) { this.deadlineImportant = deadlineImportant; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }
    public void setLabels(String labels) { this.labels = labels; }
    public void setRevisionCount(int revisionCount) { this.revisionCount = revisionCount; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setColorPrint(Boolean colorPrint) { this.colorPrint = colorPrint; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
