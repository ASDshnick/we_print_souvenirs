package com.weprintsouvenirs.we_print_souvenirs.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // личный кабинет
    @GetMapping("/user/profile")
    public String profilePage() {
        return "forward:/profile.html";
    }

    // мои заказы
    @GetMapping("/user/orders")
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
}
