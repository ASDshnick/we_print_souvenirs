package com.weprintsouvenirs.we_print_souvenirs.chat.repository;

import com.weprintsouvenirs.we_print_souvenirs.chat.model.ChatMessageEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findByOrderIdOrderBySentAtAsc(Long orderId);

    void deleteByOrder(OrderEntity order);
}