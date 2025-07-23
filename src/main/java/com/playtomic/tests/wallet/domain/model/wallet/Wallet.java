package com.playtomic.tests.wallet.domain.model.wallet;

import com.playtomic.tests.wallet.domain.model.shared.vo.Version;
import com.playtomic.tests.wallet.domain.model.wallet.vo.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Wallet {

  private final WalletId id;
  private Balance balance;
  private final Currency currency;
  private final Version version;
  private Audit audit;

  public Wallet(String currency) {
    this.id = new WalletId(UUID.randomUUID());
    this.balance = new Balance(BigDecimal.ZERO);
    this.currency = new Currency(currency);
    this.version = new Version();
    this.audit = new Audit();
  }

  public Wallet(
      String id,
      BigDecimal balance,
      String currency,
      Long version,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    this.id = new WalletId(id);
    this.balance = new Balance(balance);
    this.currency = new Currency(currency);
    this.version = new Version(version);
    this.audit = new Audit(createdAt, updatedAt, deletedAt);
  }

  public UUID id() {
    return this.id.uuid();
  }

  public BigDecimal balance() {
    return this.balance.amount();
  }

  public String currency() {
    return this.currency.currencyCode().toString();
  }

  public Long version() {
    return this.version.value();
  }

  public LocalDateTime createdAt() {
    return this.audit.createdAt();
  }

  public LocalDateTime updatedAt() {
    return this.audit.updatedAt();
  }

  public LocalDateTime deletedAt() {
    return this.audit.deletedAt();
  }

  public void deposit(BigDecimal amount) {
    this.balance = this.balance.add(amount);
    this.audit = new Audit(this.audit.createdAt(), LocalDateTime.now(), null);
  }
}
