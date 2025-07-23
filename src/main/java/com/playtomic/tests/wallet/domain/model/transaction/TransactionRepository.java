package com.playtomic.tests.wallet.domain.model.transaction;

import java.util.UUID;

public interface TransactionRepository {

  Page<Transaction> findTransactionsByWalletId(UUID walletId, int page, int size);

  Transaction save(Transaction transaction);
}
