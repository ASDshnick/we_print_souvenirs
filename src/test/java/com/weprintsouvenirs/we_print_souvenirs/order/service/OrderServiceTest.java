package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderUpdateRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AllUserOrdersDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.CheckoutRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderDetailsResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Payment;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.PaymentStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Status;
import com.weprintsouvenirs.we_print_souvenirs.order.model.CartEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderItemEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.CartRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderItemRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private UserEntity user;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(4L);
        user.setUsername("alice");
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPhone("+100");
        user.setRole(Role.USER);
        product = new ProductEntity(21L, "Mug", "Ceramic", 500);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "password")
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkoutCreatesOrderFromCartAndClearsCart() {
        CartEntity cartItem = new CartEntity(1L, product, 3, Size.SMALL, Color.WHITE, 450, "Logo");
        cartItem.setUser(user);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity order = invocation.getArgument(0);
            order.setId(100L);
            order.setCreatedAt(LocalDateTime.of(2026, 1, 1, 12, 0));
            return order;
        });

        OrderEntity order = orderService.checkout(new CheckoutRequestDTO(null, null, null, Payment.CASH));

        assertThat(order.getId()).isEqualTo(100L);
        assertThat(order.getCustomerUsername()).isEqualTo("alice");
        assertThat(order.getCustomerEmail()).isEqualTo("alice@example.com");
        assertThat(order.getPaymentMethod()).isEqualTo(Payment.CASH);
        assertThat(order.getStatus()).isEqualTo(Status.NEW);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.NOT_PAID);
        assertThat(order.getTotalAmount()).isEqualTo(1350);
        verify(cartRepository).deleteByUser(user);

        ArgumentCaptor<OrderItemEntity> itemCaptor = ArgumentCaptor.forClass(OrderItemEntity.class);
        verify(orderItemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getProduct()).isSameAs(product);
        assertThat(itemCaptor.getValue().getComment()).isEqualTo("Logo");
    }

    @Test
    void checkoutRejectsEmptyCart() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.checkout(new CheckoutRequestDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cart is empty");
    }

    @Test
    void getOrdersForUserSortsNewestFirstAndMapsProductIds() {
        OrderEntity oldOrder = order(1L, 300, LocalDateTime.of(2026, 1, 1, 10, 0));
        OrderEntity newOrder = order(2L, 600, LocalDateTime.of(2026, 1, 2, 10, 0));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(oldOrder, newOrder));
        when(orderItemRepository.findByOrder(oldOrder)).thenReturn(List.of(orderItem(10L, oldOrder, product)));
        when(orderItemRepository.findByOrder(newOrder)).thenReturn(List.of(orderItem(11L, newOrder, product)));

        List<AllUserOrdersDTO> orders = orderService.getOrdersForUser();

        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getId()).isEqualTo(2L);
        assertThat(orders.get(0).getProductsIds()).containsExactly(21L);
        assertThat(orders.get(1).getId()).isEqualTo(1L);
    }

    @Test
    void getOrderDetailsForCurrentUserRejectsForeignOrder() {
        UserEntity other = new UserEntity();
        other.setId(99L);
        OrderEntity order = order(8L, 500, LocalDateTime.now());
        order.setUser(other);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(orderRepository.findById(8L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrderDetailsForCurrentUser(8L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Access denied");
    }

    @Test
    void getOrderDetailsForAdminMapsUserAndItems() {
        OrderEntity order = order(5L, 900, LocalDateTime.of(2026, 2, 1, 9, 30));
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrder(order)).thenReturn(List.of(orderItem(1L, order, product)));

        OrderDetailsResponseDTO dto = orderService.getOrderDetailsForAdmin(5L);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getUser().getUsername()).isEqualTo("alice");
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getTotalPrice()).isEqualTo(1000);
    }

    @Test
    void getAllOrdersForAdminSortsAndMapsProductIds() {
        OrderEntity first = order(1L, 100, LocalDateTime.now());
        OrderEntity second = order(2L, 200, LocalDateTime.now());
        when(orderRepository.findAll()).thenReturn(List.of(first, second));
        when(orderItemRepository.findByOrder(first)).thenReturn(List.of(orderItem(1L, first, product)));
        when(orderItemRepository.findByOrder(second)).thenReturn(List.of(orderItem(2L, second, product)));

        List<OrderResponseDTO> response = orderService.getAllOrdersForAdmin();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getId()).isEqualTo(2L);
        assertThat(response.get(0).getProductIds()).containsExactly(21L);
    }

    @Test
    void updateOrderForAdminChangesAllowedFields() {
        OrderEntity order = order(7L, 1000, LocalDateTime.now());
        AdminOrderUpdateRequestDTO request = new AdminOrderUpdateRequestDTO(Status.DONE, PaymentStatus.PAID, "Ready");
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrder(order)).thenReturn(List.of(orderItem(1L, order, product)));
        when(orderRepository.save(order)).thenReturn(order);

        OrderResponseDTO response = orderService.updateOrderForAdmin(7L, request);

        assertThat(response.getStatus()).isEqualTo(Status.DONE);
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(order.getAdminNote()).isEqualTo("Ready");
    }

    private OrderEntity order(Long id, int total, LocalDateTime createdAt) {
        return new OrderEntity(id, user, "alice", "alice@example.com", total, Status.NEW, Payment.CARD,
                createdAt, PaymentStatus.NOT_PAID, null);
    }

    private OrderItemEntity orderItem(Long id, OrderEntity order, ProductEntity itemProduct) {
        return new OrderItemEntity(id, order, itemProduct, 2, Size.SMALL, Color.WHITE, 500, "Logo");
    }
}
