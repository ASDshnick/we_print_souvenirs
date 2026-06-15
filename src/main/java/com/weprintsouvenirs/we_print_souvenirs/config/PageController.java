package com.weprintsouvenirs.we_print_souvenirs.config;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // личный кабинет
    @GetMapping(value = "/user/profile", produces = MediaType.TEXT_HTML_VALUE)
    public String profilePage() {
        return "forward:/profile.html";
    }

    // мои заказы
    @GetMapping(value = "/user/orders", produces = MediaType.TEXT_HTML_VALUE)
    public String ordersPage() {
        return "forward:/orders.html";
    }

    // редактирование профиля
    @GetMapping("/user/change-data")
    public String changeDataPage() {
        return "forward:/edit-profile.html";
    }

    // страница входа
    @GetMapping("/user/login")
    public String loginPage() {
        return "forward:/index.html";
    }

    // страница регистрации
    @GetMapping("/user/register")
    public String registerPage() {
        return "forward:/index.html";
    }

    // чат по заказу
    @GetMapping("/chat/{orderId}")
    public String chatPage() {
        return "forward:/orders.html";
    }

    // детали заказа (пользователь)
    @GetMapping(value = "/user/orders/{orderId}", produces = MediaType.TEXT_HTML_VALUE)
    public String orderDetailPage() {
        return "forward:/order-detail.html";
    }

    // /admin -> редирект на /admin/users
    @GetMapping("/admin")
    public String adminRedirect() {
        return "redirect:/admin/users";
    }

    // страница пользователей (админ)
    @GetMapping(value = "/admin/users", produces = MediaType.TEXT_HTML_VALUE)
    public String adminUsersPage() {
        return "forward:/admin.html";
    }

    // детали пользователя (админ)
    @GetMapping(value = "/admin/users/{userId}", produces = MediaType.TEXT_HTML_VALUE)
    public String adminUserDetailPage() {
        return "forward:/admin-user.html";
    }

    // страница заказов (админ)
    @GetMapping(value = "/admin/orders", produces = MediaType.TEXT_HTML_VALUE)
    public String adminOrdersPage() {
        return "forward:/admin-orders.html";
    }

    // детали заказа (админ)
    @GetMapping(value = "/admin/orders/{orderId}", produces = MediaType.TEXT_HTML_VALUE)
    public String adminOrderDetailPage() {
        return "forward:/admin-orders.html";
    }

    // чат заказа (админ)
    @GetMapping(value = "/admin/orders/{orderId}/chat", produces = MediaType.TEXT_HTML_VALUE)
    public String adminOrderChatPage() {
        return "forward:/admin-chat.html";
    }
}