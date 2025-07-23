package com.playtomic.tests.wallet.helper;

import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WalletTestBuilder {

  private String id = UUID.randomUUID().toString();
  private BigDecimal balance = BigDecimal.ZERO;
  private String currency = "EUR";
  private Long version = null;
  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt = null;
  private LocalDateTime deletedAt = null;

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

  public WalletTestBuilder withCurrency(String currency) {
    this.currency = currency;
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

  public Wallet build() {
    return new Wallet(id, balance, currency, version, createdAt, updatedAt, deletedAt);
  }
}
