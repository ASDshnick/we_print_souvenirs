package com.weprintsouvenirs.we_print_souvenirs.order.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pricing_rules")
public class PricingRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "rule_type")
    private String ruleType;

    @Column(name = "key_value")
    private String keyValue;

    @Column(name = "max_value")
    private String maxValue;

    @Column(name = "adjustment")
    private int adjustment;

    public PricingRuleEntity() {
    }

    public PricingRuleEntity(Long id, Long productId, String ruleType, String keyValue, String maxValue, int adjustment) {
        this.id = id;
        this.productId = productId;
        this.ruleType = ruleType;
        this.keyValue = keyValue;
        this.maxValue = maxValue;
        this.adjustment = adjustment;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getRuleType() {
        return ruleType;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public int getAdjustment() {
        return adjustment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public void setAdjustment(int adjustment) {
        this.adjustment = adjustment;
    }
}
