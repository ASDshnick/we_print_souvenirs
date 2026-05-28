package com.weprintsouvenirs.we_print_souvenirs.order.repository;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;
import com.weprintsouvenirs.we_print_souvenirs.order.model.CartEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUserAndProductAndSizeAndColor(
            UserEntity user,
            ProductEntity product,
            Size size,
            Color color
    );

    List<CartEntity> findByUser(UserEntity user);

    void deleteByUser(UserEntity user);
}
