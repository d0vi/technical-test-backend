package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.transaction;

import com.playtomic.tests.wallet.domain.model.transaction.Page;
import com.playtomic.tests.wallet.domain.model.transaction.Transaction;
import java.sql.Timestamp;

public class TransactionEntityMapper {

  public Page<Transaction> toDomain(org.springframework.data.domain.Page<TransactionEntity> page) {
    return new Page<>(
        page.getContent().stream().map(this::toDomain).toList(),
        page.getNumber(),
        page.getTotalPages(),
        page.getTotalElements(),
        page.hasNext(),
        page.hasPrevious());
  }

  public TransactionEntity toEntity(Transaction transaction) {
    return new TransactionEntity(
        transaction.id(),
        transaction.walletId(),
        transaction.type(),
        transaction.amount(),
        transaction.paymentId(),
        Timestamp.valueOf(transaction.createdAt()),
        transaction.version());
  }

  public Transaction toDomain(TransactionEntity transaction) {
    return new Transaction(
        transaction.getId().toString(),
        transaction.getWalletId().toString(),
        transaction.getType(),
        transaction.getAmount(),
        transaction.getPaymentId(),
        transaction.getVersion(),
        transaction.getCreatedAt().toLocalDateTime());
  }
}
