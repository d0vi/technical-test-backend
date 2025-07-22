package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidTransactionIdException extends RuntimeException {

  public InvalidTransactionIdException() {
    super("Transaction id must not be null or empty");
  }

  public InvalidTransactionIdException(String value) {
    super(value + " is not a valid transaction id");
  }
}
