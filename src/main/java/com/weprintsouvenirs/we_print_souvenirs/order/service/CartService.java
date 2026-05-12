package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartItemRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.CartEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.CartRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.ProductRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PriceCalculatorService priceCalculatorService;

    public CartService(CartRepository cartRepository, ProductRepository productRepository, PriceCalculatorService priceCalculatorService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.priceCalculatorService = priceCalculatorService;
    }

    @Transactional
    public void addToCart(UserEntity user, CartItemRequestDTO dto) {
        ProductEntity product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Поиск такой же позиции в корзине
        CartEntity cartItem = cartRepository.findByUserAndProductAndSizeAndColor(
                user,
                product,
                dto.getSize(),
                dto.getColor()
        ).orElse(null);

        int finalQuantity = (cartItem != null) ? cartItem.getQuantity() + dto.getQuantity() : dto.getQuantity();

        int pricePerItem = priceCalculatorService.calculatePrice(product, dto.getSize(), dto.getColor(), finalQuantity);

        if (cartItem != null) {
            cartItem.setQuantity(finalQuantity);
            cartItem.setPricePerItem(pricePerItem);
            cartRepository.save(cartItem);
        } else {
            CartEntity newItem = new CartEntity();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(dto.getQuantity());
            newItem.setSize(dto.getSize());
            newItem.setColor(dto.getColor());
            newItem.setPricePerItem(pricePerItem);
            cartRepository.save(newItem);
        }
    }
}
