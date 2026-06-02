package com.weprintsouvenirs.we_print_souvenirs.chat.dto;

import java.time.LocalDateTime;

public class ChatMessageResponseDTO {
    private Long id;
    private Long orderId;
    private String senderUsername;
    private String content;
    private LocalDateTime sentAt;
    private boolean fromAdmin;

    public ChatMessageResponseDTO() {
    }

    public ChatMessageResponseDTO(Long id, Long orderId, String senderUsername, String content, LocalDateTime sentAt, boolean fromAdmin) {
        this.id = id;
        this.orderId = orderId;
        this.senderUsername = senderUsername;
        this.content = content;
        this.sentAt = sentAt;
        this.fromAdmin = fromAdmin;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isFromAdmin() {
        return fromAdmin;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void setFromAdmin(boolean fromAdmin) {
        this.fromAdmin = fromAdmin;
    }
}
