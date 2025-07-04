package com.playtomic.tests.wallet.domain.model.wallet.vo;

import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidAuditStateException;
import java.time.LocalDateTime;

public record Audit(LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {

  public Audit {
    if (createdAt == null) {
      throw new InvalidAuditStateException("Wallet creation date must not be null");
    }
    if (updatedAt != null && createdAt.isAfter(updatedAt)) {
      throw new InvalidAuditStateException("Wallet creation date must be before modification date");
    }
    if (updatedAt != null && deletedAt != null && updatedAt.isAfter(deletedAt)) {
      throw new InvalidAuditStateException("Wallet modification date must be before deletion date");
    }
  }

  public Audit() {
    this(LocalDateTime.now(), null, null);
  }
}
