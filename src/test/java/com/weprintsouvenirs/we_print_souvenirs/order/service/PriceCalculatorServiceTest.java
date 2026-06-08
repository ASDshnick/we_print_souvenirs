package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;
import com.weprintsouvenirs.we_print_souvenirs.order.model.PricingRuleEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.PricingRuleRepository;
import com.weprintsouvenirs.we_print_souvenirs.testsupport.TestData;
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
    void calculatePriceAppliesSizeSpecificColorAndBestQuantityDiscount() {
        ProductEntity product = TestData.product(1L, 1000);
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(1L, "SIZE", "MEDIUM"))
                .thenReturn(Optional.of(rule("SIZE", "MEDIUM", 200)));
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(1L, "COLOR", "RED"))
                .thenReturn(Optional.of(rule("COLOR", "RED", 150)));
        when(pricingRuleRepository.findByProductIdAndRuleType(1L, "QUANTITY"))
                .thenReturn(List.of(
                        rule("QUANTITY", "5", 100),
                        rule("QUANTITY", "10", 250)
                ));

        int price = priceCalculatorService.calculatePrice(product, Size.MEDIUM, Color.RED, 12);

        assertThat(price).isEqualTo(1100);
    }

    @Test
    void calculatePriceUsesDefaultSizeAndDefaultBlackColorWhenOptionsMissing() {
        ProductEntity product = TestData.product(1L, 1000);
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(1L, "SIZE", "SMALL"))
                .thenReturn(Optional.of(rule("SIZE", "SMALL", 0)));
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(1L, "COLOR", "BLACK"))
                .thenReturn(Optional.of(rule("COLOR", "BLACK", 50)));
        when(pricingRuleRepository.findByProductIdAndRuleType(1L, "QUANTITY"))
                .thenReturn(List.of());

        int price = priceCalculatorService.calculatePrice(product, null, null, 1);

        assertThat(price).isEqualTo(1050);
    }

    @Test
    void calculatePriceFallsBackToOthersColorModifier() {
        ProductEntity product = TestData.product(1L, 1000);
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(1L, "SIZE", "BIG"))
                .thenReturn(Optional.empty());
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(1L, "COLOR", "BLUE"))
                .thenReturn(Optional.empty());
        when(pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(1L, "COLOR", "OTHERS"))
                .thenReturn(Optional.of(rule("COLOR", "OTHERS", 80)));
        when(pricingRuleRepository.findByProductIdAndRuleType(1L, "QUANTITY"))
                .thenReturn(List.of());

        int price = priceCalculatorService.calculatePrice(product, Size.BIG, Color.BLUE, 2);

        assertThat(price).isEqualTo(1080);
    }

    private PricingRuleEntity rule(String type, String key, int adjustment) {
        PricingRuleEntity rule = new PricingRuleEntity();
        rule.setProductId(1L);
        rule.setRuleType(type);
        rule.setKeyValue(key);
        rule.setAdjustment(adjustment);
        return rule;
    }
}
