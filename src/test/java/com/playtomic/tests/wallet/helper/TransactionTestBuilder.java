package com.playtomic.tests.wallet.helper;

import com.playtomic.tests.wallet.domain.model.wallet.Transaction;
import java.math.BigDecimal;
import java.util.UUID;

public class TransactionTestBuilder {

  private String walletId = UUID.randomUUID().toString();
  private BigDecimal amount = BigDecimal.ZERO;
  private String paymentId = UUID.randomUUID().toString();

  public TransactionTestBuilder withWalletId(UUID walletId) {
    this.walletId = walletId.toString();
    return this;
  }

  public TransactionTestBuilder withAmount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public TransactionTestBuilder withPaymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  public Transaction build() {
    return new Transaction(this.walletId, this.amount, this.paymentId);
  }
}
