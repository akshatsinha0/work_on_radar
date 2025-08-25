-- simple base schema, optimized later
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- events main table using identity and jsonb for payload
CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    producer VARCHAR(255),
    source_service VARCHAR(255),
    user_id VARCHAR(255),
    order_id VARCHAR(255),
    session_id VARCHAR(255),
    trace_id VARCHAR(255),
    idempotency_key VARCHAR(255),
    data_json JSONB NOT NULL
);

-- unique idempotency per producer(optional)
CREATE UNIQUE INDEX IF NOT EXISTS ux_events_idem ON events(idempotency_key) WHERE idempotency_key IS NOT NULL;

-- time based index for queries
CREATE INDEX IF NOT EXISTS ix_events_occurred_at ON events(occurred_at DESC);
CREATE INDEX IF NOT EXISTS ix_events_type_time ON events(type,occurred_at DESC);
CREATE INDEX IF NOT EXISTS ix_events_user_time ON events(user_id,occurred_at DESC);

-- batch operations
CREATE TABLE IF NOT EXISTS batch_operations (
    id BIGSERIAL PRIMARY KEY,
    batch_id VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_events INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    tracking_url TEXT
);

-- trigger to keep updated_at fresh
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_batch_updated ON batch_operations;
CREATE TRIGGER trg_batch_updated BEFORE UPDATE ON batch_operations FOR EACH ROW EXECUTE FUNCTION set_updated_at();

