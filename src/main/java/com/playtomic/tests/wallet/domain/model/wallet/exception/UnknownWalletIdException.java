package com.playtomic.tests.wallet.domain.model.wallet.exception;

import java.util.UUID;

public class UnknownWalletIdException extends RuntimeException {

  public UnknownWalletIdException(final UUID walletId) {
    super("Wallet " + walletId.toString() + " can not be found");
  }
}
