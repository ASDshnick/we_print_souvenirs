CREATE TABLE IF NOT EXISTS products (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    price       INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS pricing_rules (
    id          BIGSERIAL    PRIMARY KEY,
    product_id  BIGINT,
    rule_type   VARCHAR(100),
    key_value   VARCHAR(255),
    max_value   VARCHAR(255),
    adjustment  INTEGER      NOT NULL DEFAULT 0
);
