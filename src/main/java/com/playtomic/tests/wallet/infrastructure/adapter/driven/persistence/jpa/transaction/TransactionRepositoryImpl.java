package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.transaction;

import com.playtomic.tests.wallet.domain.model.transaction.Page;
import com.playtomic.tests.wallet.domain.model.transaction.Transaction;
import com.playtomic.tests.wallet.domain.model.transaction.TransactionRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public class TransactionRepositoryImpl implements TransactionRepository {

  private final TransactionJpaRepository repository;
  private final TransactionEntityMapper mapper;

  public TransactionRepositoryImpl(
      TransactionJpaRepository repository, TransactionEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Page<Transaction> findTransactionsByWalletId(UUID walletId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return this.mapper.toDomain(
        this.repository.findByWalletIdOrderByCreatedAtDesc(walletId, pageable));
  }

  @Retryable(
      retryFor = {OptimisticLockingFailureException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100))
  @Transactional
  @Override
  public Transaction save(Transaction transaction) {
    TransactionEntity entity = this.repository.save(this.mapper.toEntity(transaction));
    return this.mapper.toDomain(entity);
  }
}
