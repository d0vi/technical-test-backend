package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidPaymentIdException extends RuntimeException {

  public InvalidPaymentIdException() {
    super("Payment UUID must not be null nor empty");
  }

  public InvalidPaymentIdException(String value) {
    super("Payment UUID '" + value + "' is not valid");
  }
}
