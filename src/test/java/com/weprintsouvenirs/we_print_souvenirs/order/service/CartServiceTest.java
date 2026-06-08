package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartItemRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;
import com.weprintsouvenirs.we_print_souvenirs.order.model.CartEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.CartRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.ProductRepository;
import com.weprintsouvenirs.we_print_souvenirs.testsupport.TestData;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceCalculatorService priceCalculatorService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addToCartCreatesNewCartItemWhenSameItemDoesNotExist() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        ProductEntity product = TestData.product(10L, 1000);
        CartItemRequestDTO dto = new CartItemRequestDTO(10L, 2, Size.MEDIUM, Color.RED);

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserAndProductAndSizeAndColor(user, product, Size.MEDIUM, Color.RED))
                .thenReturn(Optional.empty());
        when(priceCalculatorService.calculatePrice(product, Size.MEDIUM, Color.RED, 2)).thenReturn(1200);

        cartService.addToCart(user, dto);

        ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).save(captor.capture());
        CartEntity saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getProduct()).isEqualTo(product);
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getPricePerItem()).isEqualTo(1200);
    }

    @Test
    void addToCartUpdatesExistingCartItemQuantityAndPrice() {
        UserEntity user = TestData.user(1L, "client", Role.USER);
        ProductEntity product = TestData.product(10L, 1000);
        CartEntity existing = new CartEntity(5L, product, 2, Size.SMALL, Color.BLACK, 1000);
        existing.setUser(user);
        CartItemRequestDTO dto = new CartItemRequestDTO(10L, 3, Size.SMALL, Color.BLACK);

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserAndProductAndSizeAndColor(user, product, Size.SMALL, Color.BLACK))
                .thenReturn(Optional.of(existing));
        when(priceCalculatorService.calculatePrice(product, Size.SMALL, Color.BLACK, 5)).thenReturn(900);

        cartService.addToCart(user, dto);

        assertThat(existing.getQuantity()).isEqualTo(5);
        assertThat(existing.getPricePerItem()).isEqualTo(900);
        verify(cartRepository).save(existing);
    }

    @Test
    void addToCartThrowsWhenProductIsMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(
                TestData.user(1L, "client", Role.USER),
                new CartItemRequestDTO(99L, 1, Size.SMALL, Color.BLACK)
        )).isInstanceOf(RuntimeException.class).hasMessage("Product not found");
    }

    @Test
    void findAllItemsInCartMapsItemsAndCalculatesTotalAmount() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        ProductEntity product = TestData.product(10L, 1000);
        CartEntity cartItem = new CartEntity(5L, product, 3, Size.MEDIUM, Color.GREEN, 0);
        cartItem.setUser(user);

        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(List.of(cartItem));
        when(priceCalculatorService.calculatePrice(product, Size.MEDIUM, Color.GREEN, 3)).thenReturn(1100);

        CartResponseDTO response = cartService.findAllItemsInCart();

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getProductName()).isEqualTo("Souvenir");
        assertThat(response.getItems().get(0).getTotalPrice()).isEqualTo(3300);
        assertThat(response.getTotalAmount()).isEqualTo(3300);
    }

    @Test
    void removeItemFromCartDeletesOnlyOwnedItem() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        ProductEntity product = TestData.product(10L, 1000);
        CartEntity cartItem = new CartEntity(5L, product, 1, Size.SMALL, Color.BLACK, 1000);
        cartItem.setUser(user);

        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(cartRepository.findById(5L)).thenReturn(Optional.of(cartItem));

        cartService.removeItemFromCart(5L);

        verify(cartRepository).delete(cartItem);
    }

    @Test
    void removeItemFromCartRejectsOtherUsersItem() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        UserEntity owner = TestData.user(2L, "other", Role.USER);
        CartEntity cartItem = new CartEntity();
        cartItem.setUser(owner);

        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(cartRepository.findById(5L)).thenReturn(Optional.of(cartItem));

        assertThatThrownBy(() -> cartService.removeItemFromCart(5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You can't remove other's item");
        verify(cartRepository, never()).delete(any());
    }
}
