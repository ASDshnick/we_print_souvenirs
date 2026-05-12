package com.weprintsouvenirs.we_print_souvenirs.order.repository;

import com.weprintsouvenirs.we_print_souvenirs.order.model.PricingRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PricingRuleRepository extends JpaRepository<PricingRuleEntity, Long> {
    Optional<PricingRuleEntity> findByProductIdAndRuleTypeAndKeyValue(
            Long productId, String ruleType, String keyValue
    );

    List<PricingRuleEntity> findByProductIdAndRuleType(
            Long productId, String ruleType
    );
}
