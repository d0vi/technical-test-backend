package com.playtomic.tests.wallet.domain.model.wallet;

import com.playtomic.tests.wallet.domain.model.wallet.vo.PaymentId;
import com.playtomic.tests.wallet.domain.model.wallet.vo.TransactionId;
import com.playtomic.tests.wallet.domain.model.wallet.vo.TransactionType;
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
  private final LocalDateTime createdAt;

  public Transaction(
      String id,
      String walletId,
      TransactionType type,
      BigDecimal amount,
      String paymentId,
      LocalDateTime createdAt) {
    this.id = new TransactionId(id);
    this.walletId = new WalletId(walletId);
    this.type = type;
    this.amount = amount;
    this.paymentId = new PaymentId(paymentId);
    this.createdAt = createdAt;
  }

  public Transaction(String walletId, BigDecimal amount, String paymentId) {
    this(
        UUID.randomUUID().toString(),
        walletId,
        TransactionType.DEPOSIT,
        amount,
        paymentId,
        LocalDateTime.now());
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

  public LocalDateTime createdAt() {
    return this.createdAt;
  }
}
