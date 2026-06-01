package com.weprintsouvenirs.we_print_souvenirs.chat.service;

import com.weprintsouvenirs.we_print_souvenirs.chat.dto.ChatMessageResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.chat.model.ChatMessageEntity;
import com.weprintsouvenirs.we_print_souvenirs.chat.repository.ChatMessageRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ChatService(ChatMessageRepository chatMessageRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Метод сохраняет сообщение в БД и возвращает DTO для отправки в чат
     *
     * @param orderId id заказа
     * @param content сообщение
     * @param username имя отправителя
     * @return {@link ChatMessageResponseDTO}
     */
    @Transactional
    public ChatMessageResponseDTO saveMessage(
            Long orderId,
            String content,
            String username
    ) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        UserEntity sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверка на то, что только владелец заказа или админ могут писать в чат
        boolean isAdmin = sender.getRole() == Role.ADMIN;
        boolean isOwner = order.getUser().getId() == sender.getId();

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Access denied");
        }

        ChatMessageEntity message = new ChatMessageEntity();
        message.setOrder(order);
        message.setSenderUsername(username);
        message.setContent(content);
        message.setFromAdmin(isAdmin);

        ChatMessageEntity saved = chatMessageRepository.save(message);
        return toDTO(saved);

    }

    private ChatMessageResponseDTO toDTO(ChatMessageEntity entity) {
        return new ChatMessageResponseDTO(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getSenderUsername(),
                entity.getContent(),
                entity.getSentAt(),
                entity.isFromAdmin()
        );
    }

    @Transactional
    public List<ChatMessageResponseDTO> getHistory(
            Long orderId,
            String username
    ) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        UserEntity requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверка на то, что только владелец заказа или админ могут писать в чат
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        boolean isOwner = order.getUser().getId() == requester.getId();

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Access denied");
        }
        return chatMessageRepository.findByOrderIdOrderBySentAtAsc(orderId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
