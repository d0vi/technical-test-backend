package com.playtomic.tests.wallet.domain.model.transaction.vo;

import com.playtomic.tests.wallet.domain.model.transaction.exception.InvalidPaymentIdException;
import java.util.UUID;

public record PaymentId(UUID uuid) {

  public PaymentId(String id) {
    if (id == null || id.isBlank()) {
      throw new InvalidPaymentIdException();
    }
    try {
      UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new InvalidPaymentIdException(id);
    }
    this(UUID.fromString(id));
  }
}
