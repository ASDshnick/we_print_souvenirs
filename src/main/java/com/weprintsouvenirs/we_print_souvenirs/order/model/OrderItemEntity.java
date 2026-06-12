package com.weprintsouvenirs.we_print_souvenirs.order.model;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity")
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private Size size;

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private Color color;

    @Column(name = "price_per_item")
    private int pricePerItem;

    @Column(name = "comment")
    private String comment;

    public OrderItemEntity() {
    }

    public OrderItemEntity(Long id, OrderEntity order, ProductEntity product, int quantity, Size size, Color color, int pricePerItem, String comment) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.pricePerItem = pricePerItem;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Size getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }

    public int getPricePerItem() {
        return pricePerItem;
    }

    public String getComment() {
        return comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setPricePerItem(int pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
