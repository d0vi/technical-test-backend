CREATE TABLE wallet (
    id UUID PRIMARY KEY,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);
