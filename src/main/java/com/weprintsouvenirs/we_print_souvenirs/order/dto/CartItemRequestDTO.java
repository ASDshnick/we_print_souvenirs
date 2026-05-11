package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;

public class CartItemRequestDTO {
    private Long productId;
    private int quantity;
    private Size size;
    private Color color;

    public CartItemRequestDTO() {
    }

    public CartItemRequestDTO(Long productId, int quantity, Size size, Color color) {
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
    }

    public Long getProductId() {
        return productId;
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

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
