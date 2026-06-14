package com.weprintsouvenirs.we_print_souvenirs.admin.controller;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderUpdateRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderDetailsResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.service.OrderService;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.AdminUserResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.AdminUserUpdateRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

    public AdminController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AdminUserResponseDTO>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsersForAdmin());
    }

    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminUserResponseDTO> getUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getUserForAdmin(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody AdminUserUpdateRequestDTO requestDTO
    ) {
        try {
            return ResponseEntity.ok(userService.updateUserForAdmin(userId, requestDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/users/{userId}/note")
    public ResponseEntity<?> updateUserNote(
            @PathVariable Long userId,
            @RequestBody AdminUserUpdateRequestDTO requestDTO
    ) {
        try {
            return ResponseEntity.ok(userService.updateUserForAdmin(userId, requestDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUserForAdmin(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    @GetMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDetailsResponseDTO> getOrder(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(orderService.getOrderDetailsForAdmin(orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable Long orderId,
            @RequestBody AdminOrderUpdateRequestDTO requestDTO
    ) {
        try {
            return ResponseEntity.ok(orderService.updateOrderForAdmin(orderId, requestDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/orders/{orderId}/note")
    public ResponseEntity<?> updateOrderNote(
            @PathVariable Long orderId,
            @RequestBody AdminOrderUpdateRequestDTO requestDTO
    ) {
        try {
            return ResponseEntity.ok(orderService.updateOrderForAdmin(orderId, requestDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}