package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.DeliveryType;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderType;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AllUserOrdersDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.testsupport.TestData;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createOrderPersistsCurrentUserOrder() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        OrderDTO dto = new OrderDTO();
        dto.setType(OrderType.PRINT_3D);
        dto.setRequirements("Print souvenir");
        dto.setPolygons(5000);
        dto.setPolygonsImportant(true);
        dto.setDeadlineImportant(true);
        dto.setDeadline("2026-06-15");
        dto.setDeliveryType(DeliveryType.PICKUP);
        dto.setQuantity(2);
        dto.setColorPrint(true);

        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEntity order = orderService.createOrder(dto);

        assertThat(order.getUser()).isEqualTo(user);
        assertThat(order.getType()).isEqualTo(OrderType.PRINT_3D);
        assertThat(order.getRequirements()).isEqualTo("Print souvenir");
        assertThat(order.getPolygons()).isEqualTo(5000);
        assertThat(order.isPolygonsImportant()).isTrue();
        assertThat(order.isDeadlineImportant()).isTrue();
        assertThat(order.getDeliveryType()).isEqualTo(DeliveryType.PICKUP);
        assertThat(order.getQuantity()).isEqualTo(2);
        assertThat(order.getColorPrint()).isTrue();
    }

    @Test
    void createOrderRejectsMissingOrderType() {
        TestData.authenticateAs("client");
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(TestData.user(1L, "client", Role.USER)));

        assertThatThrownBy(() -> orderService.createOrder(new OrderDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order type is required");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrderRejectsUnknownCurrentUser() {
        TestData.authenticateAs("missing");
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(new OrderDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void getOrdersForUserMapsOrdersAndFormatsDate() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        OrderEntity order = TestData.order(10L, user, OrderStatus.IN_PROGRESS);
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));

        List<AllUserOrdersDTO> orders = orderService.getOrdersForUser();

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getId()).isEqualTo(10L);
        assertThat(orders.get(0).getType()).isEqualTo(OrderType.MODEL_3D);
        assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        assertThat(orders.get(0).getCompletionPercentage()).isEqualTo(20);
        assertThat(orders.get(0).getDate()).isEqualTo("01.06.2026");
    }
}
