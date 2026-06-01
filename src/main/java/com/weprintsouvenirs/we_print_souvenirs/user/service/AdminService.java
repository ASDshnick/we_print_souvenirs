package com.weprintsouvenirs.we_print_souvenirs.user.service;

import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderUpdateDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.AdminUserResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
                        OrderRepository orderRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Управление пользователями (FR-18) ---

    public List<AdminUserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new AdminUserResponseDTO(
                        u.getId(), u.getUsername(), u.getEmail(),
                        u.getPhone(), u.getRole(), u.isBlocked(), u.getCreatedAt()))
                .toList();
    }

    @Transactional
    public AdminUserResponseDTO toggleBlock(Long id) {
        UserEntity user = findUserById(id);
        user.setBlocked(!user.isBlocked());
        userRepository.save(user);
        return toAdminUserDTO(user);
    }

    @Transactional
    public AdminUserResponseDTO changeRole(Long id, Role role) {
        UserEntity user = findUserById(id);
        user.setRole(role);
        userRepository.save(user);
        return toAdminUserDTO(user);
    }

    /** Генерирует временный пароль, хэширует его и возвращает в открытом виде. */
    @Transactional
    public String resetPassword(Long id) {
        UserEntity user = findUserById(id);
        String tempPassword = UUID.randomUUID().toString().substring(0, 12);
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        return tempPassword;
    }

    // --- Управление заказами (FR-15, FR-16) ---

    public List<AdminOrderResponseDTO> getAllOrders(Long userId, OrderStatus status) {
        List<OrderEntity> orders;
        if (userId != null && status != null) {
            orders = orderRepository.findByUserIdAndStatus(userId, status);
        } else if (userId != null) {
            orders = orderRepository.findByUserId(userId);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream().map(this::toAdminOrderDTO).toList();
    }

    @Transactional
    public AdminOrderResponseDTO updateOrder(Long id, AdminOrderUpdateDTO dto) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        if (dto.getStatus() != null) {
            order.setStatus(dto.getStatus());
            // Автоматически выставляем 100% при статусе READY/DELIVERED
            if (dto.getStatus() == OrderStatus.READY || dto.getStatus() == OrderStatus.DELIVERED) {
                order.setCompletionPercentage(100);
            }
        }
        if (dto.getCompletionPercentage() != null) {
            int pct = dto.getCompletionPercentage();
            if (pct < 0 || pct > 100) {
                throw new RuntimeException("Completion percentage must be between 0 and 100");
            }
            order.setCompletionPercentage(pct);
        }
        if (dto.getLabels() != null) {
            order.setLabels(dto.getLabels());
        }

        orderRepository.save(order);
        return toAdminOrderDTO(order);
    }

    // --- Вспомогательные методы ---

    private UserEntity findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    private AdminUserResponseDTO toAdminUserDTO(UserEntity u) {
        return new AdminUserResponseDTO(
                u.getId(), u.getUsername(), u.getEmail(),
                u.getPhone(), u.getRole(), u.isBlocked(), u.getCreatedAt());
    }

    private AdminOrderResponseDTO toAdminOrderDTO(OrderEntity o) {
        return new AdminOrderResponseDTO(
                o.getId(),
                o.getUser().getId(),
                o.getUser().getUsername(),
                o.getType(),
                o.getRequirements(),
                o.getStatus(),
                o.getCompletionPercentage(),
                o.getLabels(),
                o.getRevisionCount(),
                o.getDeliveryType(),
                o.getQuantity(),
                o.getColorPrint(),
                o.getCreatedAt());
    }
}
