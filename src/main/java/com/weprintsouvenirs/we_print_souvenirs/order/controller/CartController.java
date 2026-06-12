package com.weprintsouvenirs.we_print_souvenirs.order.controller;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartItemRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.service.CartService;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestBody CartItemRequestDTO dto
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.addToCart(user, dto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/items")
    public ResponseEntity<CartResponseDTO> getItemsFromCart() {
        return ResponseEntity.ok(cartService.findAllItemsInCart());
    }

    @DeleteMapping("items/{itemId}")
    public ResponseEntity<String> deleteFromCart(
        @PathVariable("itemId") Long itemId
    ) {
        cartService.removeItemFromCart(itemId);
        return ResponseEntity.status(HttpStatus.OK).body("Item removed from cart");
    }

    /**
     * Изменение уже написанного комментария к товару
     * @param itemId
     * @param payload
     * @return Сообщение о том, что комментарий обновлен
     */
    @PutMapping("/items/{itemId}/comment")
    public ResponseEntity<String> updateComment(
            @PathVariable Long itemId,
            @RequestBody Map<String, String> payload
    ) {
        String newComment = payload.get("comment");
        cartService.updateComment(itemId, newComment);
        return ResponseEntity.status(HttpStatus.OK).body("Comment updated");
    }
}
