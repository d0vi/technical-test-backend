package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidAmountException extends RuntimeException {

  public InvalidAmountException() {
    super("Deposit amount must be greater than 0");
  }
}
