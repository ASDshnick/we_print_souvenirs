package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;
import com.weprintsouvenirs.we_print_souvenirs.order.model.PricingRuleEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.PricingRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceCalculatorServiceTest {

    @Mock
    private PricingRuleRepository pricingRuleRepository;

    @InjectMocks
    private PriceCalculatorService priceCalculatorService;

    @Test
    void calculatePriceAppliesSizeColorAndLargestQuantityDiscount() {
        ProductEntity product = new ProductEntity(10L, "Cup", "Ceramic", 1000);
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(10L, "SIZE", "MEDIUM"))
                .thenReturn(Optional.of(rule("SIZE", "MEDIUM", 150)));
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(10L, "COLOR", "RED"))
                .thenReturn(Optional.of(rule("COLOR", "RED", 70)));
        when(pricingRuleRepository.findByProductIdAndRuleType(10L, "QUANTITY"))
                .thenReturn(List.of(
                        rule("QUANTITY", "5", 100),
                        rule("QUANTITY", "10", 250),
                        rule("QUANTITY", "25", 900)
                ));

        int price = priceCalculatorService.calculatePrice(product, Size.MEDIUM, Color.RED, 12);

        assertThat(price).isEqualTo(970);
    }

    @Test
    void calculatePriceUsesDefaultsWhenSizeAndColorAreMissing() {
        ProductEntity product = new ProductEntity(3L, "T-Shirt", "Cotton", 500);
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(3L, "SIZE", "SMALL"))
                .thenReturn(Optional.of(rule("SIZE", "SMALL", 0)));
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(3L, "COLOR", "BLACK"))
                .thenReturn(Optional.of(rule("COLOR", "BLACK", 30)));
        when(pricingRuleRepository.findByProductIdAndRuleType(3L, "QUANTITY"))
                .thenReturn(List.of());

        int price = priceCalculatorService.calculatePrice(product, null, null, 1);

        assertThat(price).isEqualTo(530);
    }

    @Test
    void calculatePriceFallsBackToOtherColorModifier() {
        ProductEntity product = new ProductEntity(7L, "Bag", "Canvas", 800);
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(7L, "SIZE", "BIG"))
                .thenReturn(Optional.empty());
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(7L, "COLOR", "GREEN"))
                .thenReturn(Optional.empty());
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(7L, "COLOR", "OTHERS"))
                .thenReturn(Optional.of(rule("COLOR", "OTHERS", 45)));
        when(pricingRuleRepository.findByProductIdAndRuleType(7L, "QUANTITY"))
                .thenReturn(List.of(rule("QUANTITY", "10", 100)));

        int price = priceCalculatorService.calculatePrice(product, Size.BIG, Color.GREEN, 3);

        assertThat(price).isEqualTo(845);
    }

    private PricingRuleEntity rule(String type, String key, int adjustment) {
        return new PricingRuleEntity(1L, 1L, type, key, null, adjustment);
    }
}
