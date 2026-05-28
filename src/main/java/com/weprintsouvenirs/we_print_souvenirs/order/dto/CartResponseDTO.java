package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import java.util.List;

public class CartResponseDTO {
    private List<CartItemDTO> items;
    private int totalAmount;

    public CartResponseDTO() {

    }

    public CartResponseDTO(List<CartItemDTO> items, int totalAmount) {
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
}
