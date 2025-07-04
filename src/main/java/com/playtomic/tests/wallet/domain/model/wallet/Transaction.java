package com.playtomic.tests.wallet.domain.model.wallet;

import com.playtomic.tests.wallet.domain.model.wallet.vo.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(
    UUID id,
    UUID walletId,
    TransactionType type,
    BigDecimal amount,
    String paymentId,
    LocalDateTime createdAt) {

  public Transaction(UUID walletId, BigDecimal amount, String paymentId) {
    this(
        UUID.randomUUID(),
        walletId,
        TransactionType.DEPOSIT,
        amount,
        paymentId,
        LocalDateTime.now());
  }
}
