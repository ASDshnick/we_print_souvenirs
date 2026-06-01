CREATE TABLE IF NOT EXISTS chat_messages (
    id         BIGSERIAL  PRIMARY KEY,
    order_id   BIGINT     NOT NULL REFERENCES orders(id),
    author_id  BIGINT     NOT NULL REFERENCES users(id),
    text       TEXT       NOT NULL,
    created_at TIMESTAMP  NOT NULL DEFAULT NOW()
);
