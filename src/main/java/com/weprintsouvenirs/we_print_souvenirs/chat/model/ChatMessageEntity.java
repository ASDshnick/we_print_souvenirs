package com.weprintsouvenirs.we_print_souvenirs.chat.model;

import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column(name = "sender_username")
    private String senderUsername;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "from_admin")
    private boolean fromAdmin;

    @PrePersist
    private void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    public ChatMessageEntity() {
    }

    public ChatMessageEntity(Long id, OrderEntity order, String senderUsername, String content, LocalDateTime sentAt, boolean fromAdmin) {
        this.id = id;
        this.order = order;
        this.senderUsername = senderUsername;
        this.content = content;
        this.sentAt = sentAt;
        this.fromAdmin = fromAdmin;
    }

    public Long getId() {
        return id;
    }

    public OrderEntity getOrder() {
        return order;
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

    public void setOrder(OrderEntity order) {
        this.order = order;
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
