package com.weprintsouvenirs.we_print_souvenirs.controller;

import com.weprintsouvenirs.we_print_souvenirs.chat.controller.ChatController;
import com.weprintsouvenirs.we_print_souvenirs.chat.dto.ChatMessageIncomingDTO;
import com.weprintsouvenirs.we_print_souvenirs.chat.dto.ChatMessageResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.chat.service.ChatService;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderType;
import com.weprintsouvenirs.we_print_souvenirs.order.controller.OrderController;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.AdminOrderUpdateDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.OrderDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.service.OrderService;
import com.weprintsouvenirs.we_print_souvenirs.testsupport.TestData;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.controller.AdminController;
import com.weprintsouvenirs.we_print_souvenirs.user.controller.UserController;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.AdminUserResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.ChangePasswordRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.ProfileResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UserRegisterDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.service.AdminService;
import com.weprintsouvenirs.we_print_souvenirs.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @Mock
    private AdminService adminService;

    @Mock
    private ChatService chatService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void userControllerReturnsCreatedForRegistrationAndBadRequestForPasswordError() {
        UserController controller = new UserController(userService, orderService);
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        when(userService.registerUser(registerDTO)).thenReturn(TestData.user(1L, "client", Role.USER));

        assertThat(controller.registerUser(registerDTO).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ChangePasswordRequestDTO passwordDTO = new ChangePasswordRequestDTO("old", "new");
        doThrow(new RuntimeException("Old password is incorrect")).when(userService).changePassword(passwordDTO);

        assertThat(controller.changePassword(passwordDTO).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(controller.changePassword(passwordDTO).getBody()).isEqualTo("Old password is incorrect");
    }

    @Test
    void userControllerReturnsProfile() {
        UserController controller = new UserController(userService, orderService);
        when(userService.getUserProfile()).thenReturn(new ProfileResponseDTO("client", "client@example.com", "+7"));

        assertThat(controller.getUserProfile().getBody().getUsername()).isEqualTo("client");
    }

    @Test
    void adminControllerReturnsNotFoundForMissingUserAndBadRequestForBadOrderUpdate() {
        AdminController controller = new AdminController(adminService);
        when(adminService.toggleBlock(1L)).thenThrow(new RuntimeException("User not found"));
        when(adminService.updateOrder(eq(2L), any(AdminOrderUpdateDTO.class)))
                .thenThrow(new RuntimeException("bad"));

        assertThat(controller.toggleBlock(1L).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(controller.updateOrder(2L, new AdminOrderUpdateDTO()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void adminControllerReturnsUsersAndOrders() {
        AdminController controller = new AdminController(adminService);
        AdminUserResponseDTO user = new AdminUserResponseDTO(
                1L, "admin", "admin@example.com", null,
                Role.ADMIN, false, LocalDateTime.now()
        );
        AdminOrderResponseDTO order = new AdminOrderResponseDTO(
                10L, 1L, "client", OrderType.MODEL_3D,
                "requirements", OrderStatus.NEW, 0,
                null, 0, null, null, null, LocalDateTime.now()
        );
        when(adminService.getAllUsers()).thenReturn(List.of(user));
        when(adminService.getAllOrders(null, null)).thenReturn(List.of(order));

        assertThat(controller.getAllUsers().getBody()).hasSize(1);
        assertThat(controller.getAllOrders(null, null).getBody()).hasSize(1);
    }

    @Test
    void orderControllerReturnsCreatedAndBadRequest() {
        OrderController controller = new OrderController(orderService);
        UserEntity user = TestData.user(1L, "client", Role.USER);
        OrderEntity order = TestData.order(10L, user, OrderStatus.NEW);
        OrderDTO dto = new OrderDTO();
        dto.setType(OrderType.MODEL_3D);

        when(orderService.createOrder(dto)).thenReturn(order);
        assertThat(controller.createOrder(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.createOrder(dto).getBody().getId()).isEqualTo(10L);

        OrderDTO badDto = new OrderDTO();
        when(orderService.createOrder(badDto)).thenThrow(new RuntimeException("Order type is required"));
        assertThat(controller.createOrder(badDto).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void chatControllerHandlesUnauthorizedHistoryAndSendsWebSocketMessage() {
        ChatController controller = new ChatController(chatService, messagingTemplate);
        assertThat(controller.getHistory(10L, null).getStatusCode().value()).isEqualTo(401);

        Principal principal = () -> "client";
        ChatMessageResponseDTO response = new ChatMessageResponseDTO(
                1L, 10L, "client", "Hi", LocalDateTime.now(), false
        );
        ChatMessageIncomingDTO incoming = new ChatMessageIncomingDTO();
        incoming.setContent("Hi");
        when(chatService.saveMessage(10L, "Hi", "client")).thenReturn(response);

        controller.sendMessage(10L, incoming, principal);

        verify(messagingTemplate).convertAndSend("/topic/chat/10", response);
    }

    @Test
    void chatControllerRejectsAnonymousWebSocketConnection() {
        ChatController controller = new ChatController(chatService, messagingTemplate);

        assertThatThrownBy(() -> controller.sendMessage(10L, new ChatMessageIncomingDTO(), null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unauthorized WebSocket connection");
    }
}
