-- Статусы заказа: NEW, IN_PROGRESS, ON_REVIEW, READY, DELIVERED, BLOCKED
-- Типы заказа: MODEL_3D, PRINT_3D
-- Способы доставки: PICKUP, DELIVERY

CREATE TABLE IF NOT EXISTS orders (
    id                    BIGSERIAL    PRIMARY KEY,
    user_id               BIGINT       NOT NULL REFERENCES users(id),
    type                  VARCHAR(50)  NOT NULL,
    requirements          TEXT,
    polygons              INTEGER,
    polygons_important    BOOLEAN      NOT NULL DEFAULT FALSE,
    deadline_important    BOOLEAN      NOT NULL DEFAULT FALSE,
    deadline              VARCHAR(255),
    status                VARCHAR(50)  NOT NULL DEFAULT 'NEW',
    completion_percentage INTEGER      NOT NULL DEFAULT 0,
    labels                VARCHAR(500),
    revision_count        INTEGER      NOT NULL DEFAULT 0,
    delivery_type         VARCHAR(50),
    quantity              INTEGER,
    color_print           BOOLEAN,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);
