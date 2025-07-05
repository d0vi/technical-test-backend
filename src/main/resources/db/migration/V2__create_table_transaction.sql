CREATE TABLE transaction (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'PURCHASE', 'REFUND')),
    amount DECIMAL(19, 2) NOT NULL,
    payment_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallet(id)
);

CREATE INDEX idx_transaction_wallet_id ON transaction(wallet_id);
