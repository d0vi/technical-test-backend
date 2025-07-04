package com.playtomic.tests.wallet.domain.model.wallet.exception;

import java.util.UUID;

public class UnknownWalletIdException extends RuntimeException {

  public UnknownWalletIdException(final UUID walletId) {
    super("Could not found wallet with UUID '" + walletId.toString() + "'");
  }
}
