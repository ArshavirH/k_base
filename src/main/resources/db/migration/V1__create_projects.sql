-- Initial schema: UUID PKs and string-enum visibility
-- Enable pgcrypto for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS projects (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code         VARCHAR(128)  NOT NULL UNIQUE,
    name         VARCHAR(256)  NOT NULL,
    base_path    VARCHAR(1024) NOT NULL,
    domain_tags  VARCHAR(1024),
    description  VARCHAR(1024),
    visibility   VARCHAR(64)   NOT NULL,
    last_sync_at TIMESTAMP WITH TIME ZONE
);
