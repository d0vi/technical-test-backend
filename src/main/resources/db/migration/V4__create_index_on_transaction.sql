CREATE INDEX idx_transaction_wallet_id_created_at ON transaction(wallet_id, created_at DESC);
