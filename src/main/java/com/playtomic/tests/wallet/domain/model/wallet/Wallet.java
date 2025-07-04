package com.playtomic.tests.wallet.domain.model.wallet;

import com.playtomic.tests.wallet.domain.model.wallet.vo.Audit;
import com.playtomic.tests.wallet.domain.model.wallet.vo.Balance;
import com.playtomic.tests.wallet.domain.model.wallet.vo.Version;
import com.playtomic.tests.wallet.domain.model.wallet.vo.WalletId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Wallet {

  private final WalletId id;
  private Balance balance;
  private final Version version;
  private Audit audit;
  private final List<Transaction> transactions;

  public Wallet() {
    this.id = new WalletId(UUID.randomUUID());
    this.balance = new Balance(BigDecimal.ZERO);
    this.version = new Version();
    this.audit = new Audit();
    this.transactions = new ArrayList<>();
  }

  public Wallet(
      String id,
      BigDecimal balance,
      Long version,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt,
      List<Transaction> transactions) {
    this.id = new WalletId(id);
    this.balance = new Balance(balance);
    this.version = new Version(version);
    this.audit = new Audit(createdAt, updatedAt, deletedAt);
    this.transactions = new ArrayList<>(transactions);
  }

  public UUID id() {
    return this.id.uuid();
  }

  public BigDecimal balance() {
    return this.balance.amount();
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

  public List<Transaction> transactions() {
    return new ArrayList<>(transactions);
  }

  public void deposit(BigDecimal amount, String paymentId) {
    this.balance = this.balance.add(amount);
    this.audit = new Audit(this.audit.createdAt(), LocalDateTime.now(), null);

    this.transactions.add(new Transaction(this.id().toString(), amount, paymentId));
  }
}
