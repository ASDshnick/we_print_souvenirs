package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.*;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Payment;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.PaymentStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;
import com.weprintsouvenirs.we_print_souvenirs.order.model.CartEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderItemEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.CartRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderItemRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс обработки действий с заказами
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    /**
     *
     * @param requestDTO
     * JSON:
     * {
     *     "customerUsername": "username",
     *     "customerEmail": "email",
     *     "customerPhone": "+71231231212",
     *     "paymentMethod": "CARD"/"CASH"
     * }
     * @return {@link OrderEntity}
     * JSON:
     * {
     *     "id": (int),
     *     "customerUsername": "username",
     *     "customerEmail": "email",
     *     "totalAmount": (int),
     *     "status": "NEW",
     *     "paymentMethod": "CARD",
     *     "createdAt": "(LocalDateTime)"
     * }
     */
    @Transactional
    public OrderEntity checkout(CheckoutRequestDTO requestDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // товары из корзины пользователя
        List<CartEntity> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);

        order.setCustomerUsername(requestDTO.getCustomerUsername() != null
                ? requestDTO.getCustomerUsername() : user.getUsername());
        order.setCustomerEmail(requestDTO.getCustomerEmail() != null
                ? requestDTO.getCustomerEmail() : user.getEmail());

        Payment paymentMethod = requestDTO.getPaymentMethod();
        if ((paymentMethod == Payment.CARD || paymentMethod == Payment.CASH)) {
            order.setPaymentMethod(paymentMethod);
        } else {
            order.setPaymentMethod(Payment.CARD);
        }

        order.setStatus(Status.NEW);
        order.setPaymentStatus(PaymentStatus.NOT_PAID);

        // перенос товаров из корзины
        List<OrderItemEntity> orderItems = new ArrayList<>();
        int totalAmount = 0;

        for (CartEntity cartItem : cartItems) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSize(cartItem.getSize());
            orderItem.setColor(cartItem.getColor());
            orderItem.setPricePerItem(cartItem.getPricePerItem());
            orderItem.setComment(cartItem.getComment());

            orderItems.add(orderItem);
            totalAmount += cartItem.getPricePerItem() * cartItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);

        for (OrderItemEntity item : orderItems) {
            item.setOrder(order);
            orderItemRepository.save(item);
        }

        cartRepository.deleteByUser(user);

        return order;
    }

    public List<AllUserOrdersDTO> getOrdersForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderEntity> orders = orderRepository.findByUser(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        return orders.stream()
                .sorted(Comparator.comparing(OrderEntity::getId).reversed()) // сортировка заказов от новых к старым
                .map(order -> {
                    List<Long> productIds = orderItemRepository.findByOrder(order)
                            .stream()
                            .map(item -> item.getProduct().getId())
                            .collect(Collectors.toList());

                    return new AllUserOrdersDTO(
                            order.getId(),
                            order.getTotalAmount(),
                            order.getStatus(),
                            order.getCreatedAt().format(formatter),
                            productIds
                    );
                })
                .collect(Collectors.toList());
    }

    public OrderDetailsResponseDTO getOrderDetailsForCurrentUser(Long orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getUser().getId() != user.getId()) {
            throw new RuntimeException("Access denied");
        }

        return convertToDetailsDTO(order);
    }

    public List<OrderResponseDTO> getAllOrdersForAdmin() {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(OrderEntity::getId).reversed())
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderDetailsResponseDTO getOrderDetailsForAdmin(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return convertToDetailsDTO(order);
    }

    @Transactional
    public OrderResponseDTO updateOrderForAdmin(Long orderId, AdminOrderUpdateRequestDTO requestDTO) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (requestDTO.getStatus() != null) {
            order.setStatus(requestDTO.getStatus());
        }

        if (requestDTO.getPaymentStatus() != null) {
            order.setPaymentStatus(requestDTO.getPaymentStatus());
        }

        if (requestDTO.getAdminNote() != null) {
            order.setAdminNote(requestDTO.getAdminNote());
        }

        return convertToResponseDTO(orderRepository.save(order));
    }

    private OrderResponseDTO convertToResponseDTO(OrderEntity order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerUsername(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getCreatedAt(),
                order.getPaymentStatus()
        );
    }

    private OrderDetailsResponseDTO convertToDetailsDTO(OrderEntity order) {
        UserEntity user = order.getUser();

        OrderUserResponseDTO userDTO = new OrderUserResponseDTO(
                Long.valueOf(user.getId()),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );

        List<OrderItemResponseDTO> items = orderItemRepository.findByOrder(order).stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());

        return new OrderDetailsResponseDTO(
                order.getId(),
                order.getCustomerUsername(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getAdminNote(),
                order.getCreatedAt(),
                userDTO,
                items
        );
    }

    private OrderItemResponseDTO convertToItemDTO(OrderItemEntity item) {
        ProductEntity product = item.getProduct();

        return new OrderItemResponseDTO(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getDescription(),
                item.getQuantity(),
                item.getSize(),
                item.getColor(),
                item.getPricePerItem(),
                item.getPricePerItem() * item.getQuantity(),
                item.getComment()
        );
    }
}
