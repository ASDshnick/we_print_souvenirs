package com.weprintsouvenirs.we_print_souvenirs.order.repository;

import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
