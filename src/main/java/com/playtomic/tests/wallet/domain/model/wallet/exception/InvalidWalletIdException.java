package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidWalletIdException extends RuntimeException {

  public InvalidWalletIdException() {
    super("Wallet id must not be null or empty");
  }

  public InvalidWalletIdException(String value) {
    super(value + " is not a valid wallet id");
  }
}
