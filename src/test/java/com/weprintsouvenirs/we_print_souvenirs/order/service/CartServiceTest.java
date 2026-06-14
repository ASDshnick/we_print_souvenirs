package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartItemRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.dto.CartResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;
import com.weprintsouvenirs.we_print_souvenirs.order.model.CartEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.CartRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.ProductRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private UserEntity user;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(5L);
        user.setUsername("alice");
        product = new ProductEntity(11L, "Mug", "White mug", 400);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "password")
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addToCartCreatesNewItemWhenNoExistingCartLine() {
        CartItemRequestDTO request = new CartItemRequestDTO(11L, 2, Size.SMALL, Color.WHITE, "Logo");
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserAndProductAndSizeAndColor(user, product, Size.SMALL, Color.WHITE))
                .thenReturn(Optional.empty());
        when(priceCalculatorService.calculatePrice(product, Size.SMALL, Color.WHITE, 2)).thenReturn(450);

        cartService.addToCart(user, request);

        ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
        verify(cartRepository).save(captor.capture());
        CartEntity saved = captor.getValue();
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getProduct()).isSameAs(product);
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getPricePerItem()).isEqualTo(450);
        assertThat(saved.getComment()).isEqualTo("Logo");
    }

    @Test
    void addToCartUpdatesExistingLineAndComment() {
        CartEntity existing = new CartEntity(1L, product, 3, Size.SMALL, Color.WHITE, 400, "Old");
        existing.setUser(user);
        CartItemRequestDTO request = new CartItemRequestDTO(11L, 4, Size.SMALL, Color.WHITE, "New");
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserAndProductAndSizeAndColor(user, product, Size.SMALL, Color.WHITE))
                .thenReturn(Optional.of(existing));
        when(priceCalculatorService.calculatePrice(product, Size.SMALL, Color.WHITE, 7)).thenReturn(380);

        cartService.addToCart(user, request);

        verify(cartRepository).save(existing);
        assertThat(existing.getQuantity()).isEqualTo(7);
        assertThat(existing.getPricePerItem()).isEqualTo(380);
        assertThat(existing.getComment()).isEqualTo("New");
    }

    @Test
    void findAllItemsInCartReturnsRecalculatedPricesAndTotal() {
        CartEntity first = cartItem(1L, product, 2, 400, "A");
        ProductEntity secondProduct = new ProductEntity(12L, "Bag", "Canvas", 700);
        CartEntity second = cartItem(2L, secondProduct, 3, 700, "B");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(List.of(first, second));
        when(priceCalculatorService.calculatePrice(product, Size.SMALL, Color.WHITE, 2)).thenReturn(410);
        when(priceCalculatorService.calculatePrice(secondProduct, Size.SMALL, Color.WHITE, 3)).thenReturn(650);

        CartResponseDTO response = cartService.findAllItemsInCart();

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getTotalPrice()).isEqualTo(820);
        assertThat(response.getItems().get(1).getTotalPrice()).isEqualTo(1950);
        assertThat(response.getTotalAmount()).isEqualTo(2770);
    }

    @Test
    void removeItemFromCartDeletesOnlyCurrentUsersItem() {
        CartEntity item = cartItem(1L, product, 2, 400, "A");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(item));

        cartService.removeItemFromCart(1L);

        verify(cartRepository).delete(item);
    }

    @Test
    void removeItemFromCartRejectsOtherUsersItem() {
        UserEntity otherUser = new UserEntity();
        otherUser.setId(9L);
        CartEntity item = cartItem(1L, product, 2, 400, "A");
        item.setUser(otherUser);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> cartService.removeItemFromCart(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You can't remove other's item");
    }

    @Test
    void updateCommentSavesCurrentUsersItem() {
        CartEntity item = cartItem(1L, product, 2, 400, "A");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(item));

        cartService.updateComment(1L, "Updated");

        assertThat(item.getComment()).isEqualTo("Updated");
        verify(cartRepository).save(item);
    }

    private CartEntity cartItem(Long id, ProductEntity cartProduct, int quantity, int price, String comment) {
        CartEntity item = new CartEntity(id, cartProduct, quantity, Size.SMALL, Color.WHITE, price, comment);
        item.setUser(user);
        return item;
    }
}
