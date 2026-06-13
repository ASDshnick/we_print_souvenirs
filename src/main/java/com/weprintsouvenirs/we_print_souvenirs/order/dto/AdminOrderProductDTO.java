package com.weprintsouvenirs.we_print_souvenirs.order.dto;

public class AdminOrderProductDTO {
    private Long productId;
    private String productName;

    public AdminOrderProductDTO() {
    }

    public AdminOrderProductDTO(Long productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
