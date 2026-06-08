package com.weprintsouvenirs.we_print_souvenirs.user.service;

import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderUpdateDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.testsupport.TestData;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.AdminUserResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getAllUsersMapsUsersToAdminDtos() {
        when(userRepository.findAll()).thenReturn(List.of(
                TestData.user(1L, "client", Role.USER),
                TestData.user(2L, "admin", Role.ADMIN)
        ));

        List<AdminUserResponseDTO> users = adminService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getUsername()).isEqualTo("client");
        assertThat(users.get(1).getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void toggleBlockFlipsBlockedFlag() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AdminUserResponseDTO response = adminService.toggleBlock(1L);

        assertThat(response.isBlocked()).isTrue();
        assertThat(user.isBlocked()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void changeRoleUpdatesUserRole() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AdminUserResponseDTO response = adminService.changeRole(1L, Role.ADMIN);

        assertThat(response.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void resetPasswordSavesEncodedTemporaryPasswordAndReturnsPlainValue() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-temp");

        String temporaryPassword = adminService.resetPassword(1L);

        assertThat(temporaryPassword).hasSize(12);
        assertThat(user.getPassword()).isEqualTo("encoded-temp");
        verify(userRepository).save(user);
    }

    @Test
    void getAllOrdersUsesUserAndStatusFiltersTogether() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        OrderEntity order = TestData.order(10L, user, OrderStatus.READY);
        when(orderRepository.findByUserIdAndStatus(1L, OrderStatus.READY)).thenReturn(List.of(order));

        List<AdminOrderResponseDTO> orders = adminService.getAllOrders(1L, OrderStatus.READY);

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getUserId()).isEqualTo(1L);
        assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.READY);
        verify(orderRepository).findByUserIdAndStatus(1L, OrderStatus.READY);
        verify(orderRepository, never()).findAll();
    }

    @Test
    void getAllOrdersUsesSingleFiltersAndAllOrdersFallback() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        OrderEntity order = TestData.order(10L, user, OrderStatus.NEW);
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
        when(orderRepository.findByStatus(OrderStatus.NEW)).thenReturn(List.of(order));
        when(orderRepository.findAll()).thenReturn(List.of(order));

        assertThat(adminService.getAllOrders(1L, null)).hasSize(1);
        assertThat(adminService.getAllOrders(null, OrderStatus.NEW)).hasSize(1);
        assertThat(adminService.getAllOrders(null, null)).hasSize(1);

        verify(orderRepository).findByUserId(1L);
        verify(orderRepository).findByStatus(OrderStatus.NEW);
        verify(orderRepository).findAll();
    }

    @Test
    void updateOrderSetsReadyStatusToHundredPercentAndLabels() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        OrderEntity order = TestData.order(10L, user, OrderStatus.NEW);
        AdminOrderUpdateDTO dto = new AdminOrderUpdateDTO();
        dto.setStatus(OrderStatus.READY);
        dto.setLabels("urgent");

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        AdminOrderResponseDTO response = adminService.updateOrder(10L, dto);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.READY);
        assertThat(response.getCompletionPercentage()).isEqualTo(100);
        assertThat(response.getLabels()).isEqualTo("urgent");
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderRejectsInvalidCompletionPercentage() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        OrderEntity order = TestData.order(10L, user, OrderStatus.NEW);
        AdminOrderUpdateDTO dto = new AdminOrderUpdateDTO();
        dto.setCompletionPercentage(101);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> adminService.updateOrder(10L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Completion percentage must be between 0 and 100");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderThrowsWhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateOrder(99L, new AdminOrderUpdateDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found: 99");
    }
}
