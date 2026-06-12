package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;

public class CartItemDTO {
    private Long id;
    private Long productId;
    String productName;
    private int quantity;
    private Size size;
    private Color color;
    private int pricePerItem;
    private int totalPrice;
    private String comment;

    public CartItemDTO() {
    }

    public CartItemDTO(Long id, Long productId, String productName, int quantity, Size size, Color color, int pricePerItem, int totalPrice, String comment) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.pricePerItem = pricePerItem;
        this.totalPrice = totalPrice;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
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

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getComment() {
        return comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
