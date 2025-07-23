package com.playtomic.tests.wallet.domain.model.transaction.vo;

import com.playtomic.tests.wallet.domain.model.transaction.exception.InvalidTransactionIdException;
import java.util.UUID;

public record TransactionId(UUID uuid) {

  public TransactionId(String id) {
    if (id == null || id.isBlank()) {
      throw new InvalidTransactionIdException();
    }
    try {
      UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new InvalidTransactionIdException(id);
    }
    this(UUID.fromString(id));
  }
}
