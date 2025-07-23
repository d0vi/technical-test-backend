package com.playtomic.tests.wallet.domain.model.transaction;

import com.playtomic.tests.wallet.domain.model.shared.vo.Version;
import com.playtomic.tests.wallet.domain.model.transaction.vo.PaymentId;
import com.playtomic.tests.wallet.domain.model.transaction.vo.TransactionId;
import com.playtomic.tests.wallet.domain.model.transaction.vo.TransactionType;
import com.playtomic.tests.wallet.domain.model.wallet.vo.WalletId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Transaction {
  private final TransactionId id;
  private final WalletId walletId;
  private final TransactionType type;
  private final BigDecimal amount;
  private final PaymentId paymentId;
  private final Version version;
  private final LocalDateTime createdAt;

  public Transaction(
      String id,
      String walletId,
      TransactionType type,
      BigDecimal amount,
      String paymentId,
      Long version,
      LocalDateTime createdAt) {
    this.id = new TransactionId(id);
    this.walletId = new WalletId(walletId);
    this.type = type;
    this.amount = amount;
    this.paymentId = new PaymentId(paymentId);
    this.version = new Version(version);
    this.createdAt = createdAt;
  }

  public Transaction(String walletId, BigDecimal amount, String paymentId) {
    this.id = new TransactionId(UUID.randomUUID());
    this.walletId = new WalletId(walletId);
    this.type = TransactionType.DEPOSIT;
    this.amount = amount;
    this.paymentId = new PaymentId(paymentId);
    this.version = new Version();
    this.createdAt = LocalDateTime.now();
  }

  public UUID id() {
    return this.id.uuid();
  }

  public UUID walletId() {
    return this.walletId.uuid();
  }

  public TransactionType type() {
    return this.type;
  }

  public BigDecimal amount() {
    return this.amount;
  }

  public String paymentId() {
    return this.paymentId.uuid().toString();
  }

  public Long version() {
    return this.version.value();
  }

  public LocalDateTime createdAt() {
    return this.createdAt;
  }
}
