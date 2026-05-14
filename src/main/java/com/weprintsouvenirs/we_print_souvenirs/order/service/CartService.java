package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartItemDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartItemRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.model.CartEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.CartRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.ProductRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PriceCalculatorService priceCalculatorService;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository, PriceCalculatorService priceCalculatorService, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.priceCalculatorService = priceCalculatorService;
        this.userRepository = userRepository;
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

    public CartResponseDTO findAllItemsInCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartEntity> cartEntities = cartRepository.findByUser(user);

        List<CartItemDTO> items = cartEntities.stream().map(cartItem -> {
            ProductEntity product = cartItem.getProduct();

            int currentPricePerItem = priceCalculatorService.calculatePrice(
                    product,
                    cartItem.getSize(),
                    cartItem.getColor(),
                    cartItem.getQuantity()
            );

            CartItemDTO dto = new CartItemDTO();
            dto.setId(cartItem.getId());
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setQuantity(cartItem.getQuantity());
            dto.setSize(cartItem.getSize());
            dto.setColor(cartItem.getColor());
            dto.setPricePerItem(currentPricePerItem);
            dto.setTotalPrice(currentPricePerItem * cartItem.getQuantity());
            return dto;
        }).collect(Collectors.toList());

        int totalAmount = items.stream()
                .mapToInt(CartItemDTO::getTotalPrice)
                .sum();

        return new CartResponseDTO(items, totalAmount);
    }
}
