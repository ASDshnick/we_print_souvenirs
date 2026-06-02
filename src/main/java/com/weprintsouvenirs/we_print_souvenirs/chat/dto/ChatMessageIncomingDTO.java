package com.weprintsouvenirs.we_print_souvenirs.chat.dto;

public class ChatMessageIncomingDTO {
    private Long orderId;
    private String content;

    public ChatMessageIncomingDTO() {
    }

    public ChatMessageIncomingDTO(String content, Long orderId) {
        this.content = content;
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getContent() {
        return content;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
