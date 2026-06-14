package com.weprintsouvenirs.we_print_souvenirs.chat.service;

import com.weprintsouvenirs.we_print_souvenirs.chat.dto.ChatMessageResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.chat.model.ChatMessageEntity;
import com.weprintsouvenirs.we_print_souvenirs.chat.repository.ChatMessageRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    private UserEntity owner;
    private UserEntity admin;
    private UserEntity stranger;
    private OrderEntity order;

    @BeforeEach
    void setUp() {
        owner = user(1L, "owner", Role.USER);
        admin = user(2L, "admin", Role.ADMIN);
        stranger = user(3L, "stranger", Role.USER);
        order = new OrderEntity();
        order.setId(10L);
        order.setUser(owner);
    }

    @Test
    void saveMessageAllowsOrderOwner() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenAnswer(invocation -> {
            ChatMessageEntity message = invocation.getArgument(0);
            message.setId(5L);
            message.setSentAt(LocalDateTime.of(2026, 1, 1, 12, 0));
            return message;
        });

        ChatMessageResponseDTO response = chatService.saveMessage(10L, "Hello", "owner");

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getOrderId()).isEqualTo(10L);
        assertThat(response.getSenderUsername()).isEqualTo("owner");
        assertThat(response.getContent()).isEqualTo("Hello");
        assertThat(response.isFromAdmin()).isFalse();
    }

    @Test
    void saveMessageMarksAdminMessages() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenAnswer(invocation -> {
            ChatMessageEntity message = invocation.getArgument(0);
            message.setId(6L);
            message.setSentAt(LocalDateTime.of(2026, 1, 1, 12, 5));
            return message;
        });

        ChatMessageResponseDTO response = chatService.saveMessage(10L, "Answer", "admin");

        assertThat(response.isFromAdmin()).isTrue();
    }

    @Test
    void saveMessageRejectsStranger() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("stranger")).thenReturn(Optional.of(stranger));

        assertThatThrownBy(() -> chatService.saveMessage(10L, "No", "stranger"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Access denied");
    }

    @Test
    void getHistoryAllowsAdminAndMapsMessagesInRepositoryOrder() {
        ChatMessageEntity first = message(1L, "owner", "Question", false, LocalDateTime.of(2026, 1, 1, 10, 0));
        ChatMessageEntity second = message(2L, "admin", "Answer", true, LocalDateTime.of(2026, 1, 1, 10, 1));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(chatMessageRepository.findByOrderIdOrderBySentAtAsc(10L)).thenReturn(List.of(first, second));

        List<ChatMessageResponseDTO> history = chatService.getHistory(10L, "admin");

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getContent()).isEqualTo("Question");
        assertThat(history.get(1).isFromAdmin()).isTrue();
    }

    private UserEntity user(long id, String username, Role role) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        return user;
    }

    private ChatMessageEntity message(Long id, String sender, String content, boolean fromAdmin, LocalDateTime sentAt) {
        return new ChatMessageEntity(id, order, sender, content, sentAt, fromAdmin);
    }
}
