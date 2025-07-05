package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import com.playtomic.tests.wallet.domain.model.wallet.Transaction;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import java.sql.Timestamp;
import java.util.Optional;

public class WalletEntityMapper {

  public WalletEntity toEntity(Wallet wallet) {
    return new WalletEntity(
        wallet.id(),
        wallet.balance(),
        Timestamp.valueOf(wallet.createdAt()),
        Optional.ofNullable(wallet.updatedAt()).map(Timestamp::valueOf).orElse(null),
        Optional.ofNullable(wallet.deletedAt()).map(Timestamp::valueOf).orElse(null),
        wallet.version(),
        wallet.transactions().stream().map(this::toEntity).toList());
  }

  public Wallet toDomain(WalletEntity wallet) {
    return new Wallet(
        wallet.getId().toString(),
        wallet.getBalance(),
        wallet.getVersion(),
        wallet.getCreatedAt().toLocalDateTime(),
        Optional.ofNullable(wallet.getUpdatedAt()).map(Timestamp::toLocalDateTime).orElse(null),
        Optional.ofNullable(wallet.getDeletedAt()).map(Timestamp::toLocalDateTime).orElse(null),
        wallet.getTransactions().stream().map(this::toDomain).toList());
  }

  private TransactionEntity toEntity(Transaction transaction) {
    return new TransactionEntity(
        transaction.id(),
        transaction.walletId(),
        transaction.type(),
        transaction.amount(),
        transaction.paymentId(),
        Timestamp.valueOf(transaction.createdAt()));
  }

  private Transaction toDomain(TransactionEntity transaction) {
    return new Transaction(
        transaction.getId().toString(),
        transaction.getWalletId().toString(),
        transaction.getType(),
        transaction.getAmount(),
        transaction.getPaymentId(),
        transaction.getCreatedAt().toLocalDateTime());
  }
}
