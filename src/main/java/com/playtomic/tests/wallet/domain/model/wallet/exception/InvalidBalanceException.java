package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidBalanceException extends RuntimeException {

  public InvalidBalanceException() {
    super("Wallet balance can not be null");
  }
}
