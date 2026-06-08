package com.weprintsouvenirs.we_print_souvenirs.chat.service;

import com.weprintsouvenirs.we_print_souvenirs.chat.dto.ChatMessageResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.chat.model.ChatMessageEntity;
import com.weprintsouvenirs.we_print_souvenirs.chat.repository.ChatMessageRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.testsupport.TestData;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void saveMessageAllowsOrderOwnerAndMarksMessageAsUserMessage() {
        UserEntity owner = TestData.user(1L, "client", Role.USER);
        OrderEntity order = TestData.order(10L, owner, OrderStatus.NEW);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(owner));
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenAnswer(invocation -> {
            ChatMessageEntity message = invocation.getArgument(0);
            message.setId(100L);
            message.setSentAt(LocalDateTime.of(2026, 6, 1, 13, 0));
            return message;
        });

        ChatMessageResponseDTO response = chatService.saveMessage(10L, "Hello", "client");

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getOrderId()).isEqualTo(10L);
        assertThat(response.getSenderUsername()).isEqualTo("client");
        assertThat(response.isFromAdmin()).isFalse();
    }

    @Test
    void saveMessageAllowsAdminAndMarksMessageAsAdminMessage() {
        UserEntity owner = TestData.user(1L, "client", Role.USER);
        UserEntity admin = TestData.user(2L, "admin", Role.ADMIN);
        OrderEntity order = TestData.order(10L, owner, OrderStatus.NEW);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenAnswer(invocation -> {
            ChatMessageEntity message = invocation.getArgument(0);
            message.setId(101L);
            message.setSentAt(LocalDateTime.of(2026, 6, 1, 13, 10));
            return message;
        });

        ChatMessageResponseDTO response = chatService.saveMessage(10L, "Admin reply", "admin");

        assertThat(response.isFromAdmin()).isTrue();
        ArgumentCaptor<ChatMessageEntity> captor = ArgumentCaptor.forClass(ChatMessageEntity.class);
        verify(chatMessageRepository).save(captor.capture());
        assertThat(captor.getValue().getContent()).isEqualTo("Admin reply");
    }

    @Test
    void saveMessageRejectsUserWithoutOrderAccess() {
        UserEntity owner = TestData.user(1L, "client", Role.USER);
        UserEntity stranger = TestData.user(3L, "stranger", Role.USER);
        OrderEntity order = TestData.order(10L, owner, OrderStatus.NEW);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("stranger")).thenReturn(Optional.of(stranger));

        assertThatThrownBy(() -> chatService.saveMessage(10L, "Nope", "stranger"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Access denied");
        verify(chatMessageRepository, never()).save(any());
    }

    @Test
    void getHistoryAllowsOwnerAndReturnsMessagesOrderedByRepository() {
        UserEntity owner = TestData.user(1L, "client", Role.USER);
        OrderEntity order = TestData.order(10L, owner, OrderStatus.NEW);
        ChatMessageEntity first = new ChatMessageEntity(
                1L, order, "client", "Question",
                LocalDateTime.of(2026, 6, 1, 12, 0), false
        );
        ChatMessageEntity second = new ChatMessageEntity(
                2L, order, "admin", "Answer",
                LocalDateTime.of(2026, 6, 1, 12, 5), true
        );

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(owner));
        when(chatMessageRepository.findByOrderIdOrderBySentAtAsc(10L)).thenReturn(List.of(first, second));

        List<ChatMessageResponseDTO> history = chatService.getHistory(10L, "client");

        assertThat(history).extracting(ChatMessageResponseDTO::getContent)
                .containsExactly("Question", "Answer");
        assertThat(history.get(1).isFromAdmin()).isTrue();
    }

    @Test
    void getHistoryRejectsUnknownOrder() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.getHistory(99L, "client"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found");
    }
}
