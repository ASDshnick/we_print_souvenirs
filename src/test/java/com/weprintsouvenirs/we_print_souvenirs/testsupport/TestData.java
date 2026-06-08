package com.weprintsouvenirs.we_print_souvenirs.testsupport;

import com.weprintsouvenirs.we_print_souvenirs.order.OrderStatus;
import com.weprintsouvenirs.we_print_souvenirs.order.OrderType;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public final class TestData {

    private TestData() {
    }

    public static UserEntity user(Long id, String username, Role role) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPhone("+79990000000");
        user.setPassword("encoded-password");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.of(2026, 6, 1, 10, 0));
        return user;
    }

    public static OrderEntity order(Long id, UserEntity user, OrderStatus status) {
        OrderEntity order = new OrderEntity();
        order.setId(id);
        order.setUser(user);
        order.setType(OrderType.MODEL_3D);
        order.setRequirements("Create 3D model");
        order.setStatus(status);
        order.setCompletionPercentage(20);
        order.setRevisionCount(1);
        order.setCreatedAt(LocalDateTime.of(2026, 6, 1, 12, 0));
        return order;
    }

    public static ProductEntity product(Long id, int price) {
        ProductEntity product = new ProductEntity();
        product.setId(id);
        product.setName("Souvenir");
        product.setDescription("Test product");
        product.setPrice(price);
        return product;
    }

    public static void authenticateAs(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(username, "password")
        );
    }
}
