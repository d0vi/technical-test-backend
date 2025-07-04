package com.playtomic.tests.wallet.helper;

import com.playtomic.tests.wallet.domain.model.wallet.Transaction;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WalletTestBuilder {

  private String id = UUID.randomUUID().toString();
  private BigDecimal balance = BigDecimal.ZERO;
  private Long version = 0L;
  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt = null;
  private LocalDateTime deletedAt = null;
  private List<Transaction> transactions = new ArrayList<>();

  public WalletTestBuilder withId(String id) {
    this.id = id;
    return this;
  }

  public WalletTestBuilder withId(UUID id) {
    this.id = id.toString();
    return this;
  }

  public WalletTestBuilder withBalance(BigDecimal balance) {
    this.balance = balance;
    return this;
  }

  public WalletTestBuilder withBalance(String balance) {
    this.balance = new BigDecimal(balance);
    return this;
  }

  public WalletTestBuilder withVersion(Long version) {
    this.version = version;
    return this;
  }

  public WalletTestBuilder withCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public WalletTestBuilder withUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public WalletTestBuilder withDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
    return this;
  }

  public WalletTestBuilder withTransactions(List<Transaction> transactions) {
    this.transactions = new ArrayList<>(transactions);
    return this;
  }

  public WalletTestBuilder withTransaction(Transaction transaction) {
    this.transactions.add(transaction);
    return this;
  }

  public WalletTestBuilder withNoTransactions() {
    this.transactions = new ArrayList<>();
    return this;
  }

  public Wallet build() {
    return new Wallet(id, balance, version, createdAt, updatedAt, deletedAt, transactions);
  }
}
