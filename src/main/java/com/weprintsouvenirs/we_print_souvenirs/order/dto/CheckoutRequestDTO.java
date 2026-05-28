package com.weprintsouvenirs.we_print_souvenirs.order.dto;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Payment;

/**
 * DTO, приходящие для создания заказа
 */
public class CheckoutRequestDTO {
    private String customerUsername;
    private String customerEmail;
    private String customerPhone;
    private Payment paymentMethod;

    public CheckoutRequestDTO() {
    }

    public CheckoutRequestDTO(String customerUsername, String customerEmail, String customerPhone, Payment paymentMethod) {
        this.customerUsername = customerUsername;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.paymentMethod = paymentMethod;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public Payment getPaymentMethod() {
        return paymentMethod;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setPaymentMethod(Payment paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
