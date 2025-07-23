package com.playtomic.tests.wallet.application.usecase.transaction.read;

import com.playtomic.tests.wallet.domain.model.transaction.Page;
import com.playtomic.tests.wallet.domain.model.transaction.Transaction;
import com.playtomic.tests.wallet.domain.model.transaction.TransactionRepository;
import java.util.UUID;

public class GetTransactionsUseCase {

  private final TransactionRepository repository;

  public GetTransactionsUseCase(TransactionRepository repository) {
    this.repository = repository;
  }

  public Page<Transaction> execute(UUID walletId, int page, int size) {
    return this.repository.findTransactionsByWalletId(walletId, page, size);
  }
}
