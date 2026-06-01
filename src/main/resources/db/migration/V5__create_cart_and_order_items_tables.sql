CREATE TABLE IF NOT EXISTS cart (
    id            BIGSERIAL  PRIMARY KEY,
    user_id       BIGINT     NOT NULL REFERENCES users(id),
    product_id    BIGINT     NOT NULL REFERENCES products(id),
    quantity      INTEGER    NOT NULL DEFAULT 1,
    size          VARCHAR(50),
    color         VARCHAR(50),
    price_per_item INTEGER   NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_items (
    id             BIGSERIAL  PRIMARY KEY,
    order_id       BIGINT     NOT NULL REFERENCES orders(id),
    product_id     BIGINT     NOT NULL REFERENCES products(id),
    quantity       INTEGER    NOT NULL DEFAULT 1,
    size           VARCHAR(50),
    color          VARCHAR(50),
    price_per_item INTEGER    NOT NULL DEFAULT 0
);
