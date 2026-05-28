package com.weprintsouvenirs.we_print_souvenirs.order.controller;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.CheckoutRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(
            @RequestBody CheckoutRequestDTO checkoutRequestDTO
    ) {
        try {
            OrderEntity order = orderService.checkout(checkoutRequestDTO);
            OrderResponseDTO response = convertToDTO(order);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Приватный метод для перевода сущности заказа в возвращаемое DTO
     * @param order
     * @return {@link OrderResponseDTO}
     */
    private OrderResponseDTO convertToDTO(OrderEntity order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerUsername(order.getCustomerUsername());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }


}
