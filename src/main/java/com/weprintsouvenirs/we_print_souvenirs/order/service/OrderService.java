package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.AllUserOrdersDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Создание заказа (FR-06 — FR-09).
     * Доступно только авторизованным пользователям.
     */
    @Transactional
    public OrderEntity createOrder(OrderDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getType() == null) {
            throw new RuntimeException("Order type is required");
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setType(dto.getType());
        order.setRequirements(dto.getRequirements());
        order.setPolygons(dto.getPolygons());
        order.setPolygonsImportant(dto.isPolygonsImportant());
        order.setDeadlineImportant(dto.isDeadlineImportant());
        order.setDeadline(dto.getDeadline());

        if (dto.getDeliveryType() != null) {
            order.setDeliveryType(dto.getDeliveryType());
        }
        if (dto.getQuantity() != null) {
            order.setQuantity(dto.getQuantity());
        }
        if (dto.getColorPrint() != null) {
            order.setColorPrint(dto.getColorPrint());
        }

        return orderRepository.save(order);
    }

    /**
     * История заказов текущего пользователя (FR-10, FR-11).
     */
    public List<AllUserOrdersDTO> getOrdersForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderEntity> orders = orderRepository.findByUser(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return orders.stream()
                .map(order -> new AllUserOrdersDTO(
                        order.getId(),
                        order.getType(),
                        order.getStatus(),
                        order.getCompletionPercentage(),
                        order.getCreatedAt().format(formatter)
                ))
                .collect(Collectors.toList());
    }
}
