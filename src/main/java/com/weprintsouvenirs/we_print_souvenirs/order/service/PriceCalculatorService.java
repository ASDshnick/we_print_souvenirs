package com.weprintsouvenirs.we_print_souvenirs.order.service;

import com.weprintsouvenirs.we_print_souvenirs.order.enums.Color;
import com.weprintsouvenirs.we_print_souvenirs.order.enums.Size;
import com.weprintsouvenirs.we_print_souvenirs.order.model.PricingRuleEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.model.ProductEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.PricingRuleRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PriceCalculatorService {
    private final PricingRuleRepository pricingRuleRepository;

    public PriceCalculatorService(PricingRuleRepository pricingRuleRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
    }

    public int calculatePrice(ProductEntity product, Size size, Color color, int quantity) {
        int finalPrice = product.getPrice();

        // размер
        String sizeKey = (size != null) ? size.name() : "SMALL";
        finalPrice += getModifier(product.getId(), "SIZE", sizeKey);

        // цвет
        finalPrice += getColorModifier(product.getId(), color);

        // количество
        finalPrice -= getQuantityDiscount(product.getId(), quantity);

        return finalPrice;
    }


    private int getModifier(Long productId, String type, String key) {
        return pricingRuleRepository.findByProductIdAndRuleTypeAndKeyValue(productId, type, key)
                .map(PricingRuleEntity::getAdjustment)
                .orElse(0);
    }

    private int getColorModifier(Long productId, Color color) {
        if (color == null) {
            return getModifier(productId, "COLOR", "BLACK");
        }

        String colorName = color.name();

        Optional<Integer> specificMod = pricingRuleRepository
                .findByProductIdAndRuleTypeAndKeyValue(productId, "COLOR", colorName)
                .map(PricingRuleEntity::getAdjustment);

        if (specificMod.isPresent()) {
            return specificMod.get();
        }

        return getModifier(productId, "COLOR", "OTHERS");

    }

    private int getQuantityDiscount(Long productId, int quantity) {
        List<PricingRuleEntity> rules = pricingRuleRepository.findByProductIdAndRuleType(productId, "QUANTITY");
        return rules.stream()
                .filter(r -> quantity >= Integer.parseInt(r.getKeyValue()))
                .max(Comparator.comparingInt(r -> Integer.parseInt(r.getKeyValue())))
                .map(PricingRuleEntity::getAdjustment)
                .orElse(0);
    }
}
