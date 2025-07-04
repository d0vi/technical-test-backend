package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidTransactionIdException extends RuntimeException {

  public InvalidTransactionIdException() {
    super("Transaction UUID must not be null nor empty");
  }

  public InvalidTransactionIdException(String value) {
    super("Transaction UUID '" + value + "' is not valid");
  }
}
