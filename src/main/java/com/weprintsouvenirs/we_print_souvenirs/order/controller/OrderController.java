package com.weprintsouvenirs.we_print_souvenirs.order.controller;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Создать заказ (FR-06 — FR-09) */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderDTO dto) {
        try {
            OrderEntity order = orderService.createOrder(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private OrderResponseDTO toDTO(OrderEntity order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setType(order.getType());
        dto.setRequirements(order.getRequirements());
        dto.setStatus(order.getStatus());
        dto.setCompletionPercentage(order.getCompletionPercentage());
        dto.setLabels(order.getLabels());
        dto.setRevisionCount(order.getRevisionCount());
        dto.setDeliveryType(order.getDeliveryType());
        dto.setQuantity(order.getQuantity());
        dto.setColorPrint(order.getColorPrint());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
