-- Align old local schemas created before the current entity model.
-- V1-V5 use CREATE TABLE IF NOT EXISTS, so they do not alter tables that already existed.

ALTER TABLE orders ADD COLUMN IF NOT EXISTS type VARCHAR(50);
UPDATE orders SET type = 'MODEL_3D' WHERE type IS NULL;
ALTER TABLE orders ALTER COLUMN type SET DEFAULT 'MODEL_3D';
ALTER TABLE orders ALTER COLUMN type SET NOT NULL;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS requirements TEXT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS polygons INTEGER;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS polygons_important BOOLEAN;
UPDATE orders SET polygons_important = FALSE WHERE polygons_important IS NULL;
ALTER TABLE orders ALTER COLUMN polygons_important SET DEFAULT FALSE;
ALTER TABLE orders ALTER COLUMN polygons_important SET NOT NULL;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS deadline_important BOOLEAN;
UPDATE orders SET deadline_important = FALSE WHERE deadline_important IS NULL;
ALTER TABLE orders ALTER COLUMN deadline_important SET DEFAULT FALSE;
ALTER TABLE orders ALTER COLUMN deadline_important SET NOT NULL;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS deadline VARCHAR(255);

ALTER TABLE orders ADD COLUMN IF NOT EXISTS status VARCHAR(50);
UPDATE orders SET status = 'IN_PROGRESS' WHERE status = 'IN_PROCESS';
UPDATE orders SET status = 'DELIVERED' WHERE status IN ('DELIVERY', 'DONE');
UPDATE orders SET status = 'BLOCKED' WHERE status = 'CANCEL';
UPDATE orders SET status = 'NEW' WHERE status IS NULL;
ALTER TABLE orders ALTER COLUMN status SET DEFAULT 'NEW';
ALTER TABLE orders ALTER COLUMN status SET NOT NULL;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS completion_percentage INTEGER;
UPDATE orders SET completion_percentage = 0 WHERE completion_percentage IS NULL;
ALTER TABLE orders ALTER COLUMN completion_percentage SET DEFAULT 0;
ALTER TABLE orders ALTER COLUMN completion_percentage SET NOT NULL;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS labels VARCHAR(500);

ALTER TABLE orders ADD COLUMN IF NOT EXISTS revision_count INTEGER;
UPDATE orders SET revision_count = 0 WHERE revision_count IS NULL;
ALTER TABLE orders ALTER COLUMN revision_count SET DEFAULT 0;
ALTER TABLE orders ALTER COLUMN revision_count SET NOT NULL;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivery_type VARCHAR(50);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS quantity INTEGER;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS color_print BOOLEAN;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
UPDATE orders SET created_at = NOW() WHERE created_at IS NULL;
ALTER TABLE orders ALTER COLUMN created_at SET DEFAULT NOW();
ALTER TABLE orders ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS sender_username VARCHAR(255);
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS content TEXT;
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS sent_at TIMESTAMP;
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS from_admin BOOLEAN;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'chat_messages'
          AND column_name = 'text'
    ) THEN
        EXECUTE 'UPDATE chat_messages SET content = text WHERE content IS NULL';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'chat_messages'
          AND column_name = 'created_at'
    ) THEN
        EXECUTE 'UPDATE chat_messages SET sent_at = created_at WHERE sent_at IS NULL';
    END IF;
END $$;

UPDATE chat_messages SET sent_at = NOW() WHERE sent_at IS NULL;
UPDATE chat_messages SET from_admin = FALSE WHERE from_admin IS NULL;
ALTER TABLE chat_messages ALTER COLUMN sent_at SET DEFAULT NOW();
ALTER TABLE chat_messages ALTER COLUMN from_admin SET DEFAULT FALSE;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'chat_messages'
          AND column_name = 'author_id'
    ) THEN
        ALTER TABLE chat_messages ALTER COLUMN author_id DROP NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'chat_messages'
          AND column_name = 'text'
    ) THEN
        ALTER TABLE chat_messages ALTER COLUMN text DROP NOT NULL;
    END IF;
END $$;
