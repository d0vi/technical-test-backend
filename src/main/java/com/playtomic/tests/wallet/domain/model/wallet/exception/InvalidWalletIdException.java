package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidWalletIdException extends RuntimeException {

  public InvalidWalletIdException() {
    super("Wallet UUID must not be null nor empty");
  }

  public InvalidWalletIdException(String value) {
    super("Wallet UUID '" + value + "' is not valid");
  }
}
