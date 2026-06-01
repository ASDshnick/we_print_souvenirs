package com.weprintsouvenirs.we_print_souvenirs.user.controller;

import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderUpdateDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.AdminUserResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UpdateUserRoleDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Административная панель (FR-15 — FR-18).
 * Все эндпоинты доступны только пользователям с ролью ADMIN.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ===== Пользователи (FR-18) =====

    /** Получить список всех пользователей */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /** Заблокировать / разблокировать пользователя */
    @PutMapping("/users/{id}/block")
    public ResponseEntity<AdminUserResponseDTO> toggleBlock(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.toggleBlock(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Изменить роль пользователя (USER / ADMIN) */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<AdminUserResponseDTO> changeRole(
            @PathVariable Long id,
            @RequestBody UpdateUserRoleDTO dto) {
        try {
            return ResponseEntity.ok(adminService.changeRole(id, dto.getRole()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Сбросить пароль пользователя — возвращает временный пароль */
    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id) {
        try {
            String tempPassword = adminService.resetPassword(id);
            return ResponseEntity.ok(Map.of("tempPassword", tempPassword));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== Заказы (FR-15, FR-16) =====

    /**
     * Получить список заказов.
     * Опциональные фильтры: userId, status
     */
    @GetMapping("/orders")
    public ResponseEntity<List<AdminOrderResponseDTO>> getAllOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(adminService.getAllOrders(userId, status));
    }

    /**
     * Обновить статус, процент выполнения и/или метки заказа.
     * При статусе READY/DELIVERED процент автоматически выставляется в 100.
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<AdminOrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody AdminOrderUpdateDTO dto) {
        try {
            return ResponseEntity.ok(adminService.updateOrder(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
