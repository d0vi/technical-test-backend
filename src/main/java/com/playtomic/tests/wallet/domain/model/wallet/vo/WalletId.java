package com.playtomic.tests.wallet.domain.model.wallet.vo;

import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidWalletIdException;
import java.util.UUID;

public record WalletId(UUID uuid) {

  public WalletId(String id) {
    if (id == null || id.isBlank()) {
      throw new InvalidWalletIdException();
    }
    try {
      UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new InvalidWalletIdException(id);
    }
    this(UUID.fromString(id));
  }
}
