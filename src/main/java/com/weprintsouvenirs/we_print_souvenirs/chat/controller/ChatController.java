package com.weprintsouvenirs.we_print_souvenirs.chat.controller;

import com.weprintsouvenirs.we_print_souvenirs.chat.dto.ChatMessageIncomingDTO;
import com.weprintsouvenirs.we_print_souvenirs.chat.dto.ChatMessageResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.chat.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {


    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{orderId}")
    public void sendMessage(
            @DestinationVariable Long orderId,
            @Payload ChatMessageIncomingDTO dto,
            Principal principal
    ) {
        log.info("=== WS sendMessage called ===");
        log.info("@DestinationVariable orderId = {}", orderId);
        log.info("dto.orderId = {}", dto.getOrderId());
        log.info("dto.content = {}", dto.getContent());
        log.info("principal = {}", principal != null ? principal.getName() : "NULL");

        if (principal == null) {
            log.error("Principal is null — JWT not authenticated over WebSocket");
            throw new RuntimeException("Unauthorized WebSocket connection");
        }

        Long resolvedOrderId = (dto.getOrderId() != null) ? dto.getOrderId() : orderId;

        ChatMessageResponseDTO response = chatService.saveMessage(
                resolvedOrderId,
                dto.getContent(),
                principal.getName()
        );

        messagingTemplate.convertAndSend("/topic/chat/" + orderId, response);
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<ChatMessageResponseDTO>> getHistory(
            @PathVariable Long orderId,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            List<ChatMessageResponseDTO> history = chatService.getHistory(orderId, principal.getName());
            return ResponseEntity.status(HttpStatus.OK).body(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        }
    }
}
