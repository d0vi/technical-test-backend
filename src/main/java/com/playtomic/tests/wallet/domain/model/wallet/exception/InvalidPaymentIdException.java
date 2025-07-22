package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidPaymentIdException extends RuntimeException {

  public InvalidPaymentIdException() {
    super("Payment id must not be null or empty");
  }

  public InvalidPaymentIdException(String value) {
    super(value + " is not a valid payment id");
  }
}
