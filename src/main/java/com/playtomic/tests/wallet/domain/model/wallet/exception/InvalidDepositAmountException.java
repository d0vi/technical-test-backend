package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidDepositAmountException extends RuntimeException {

  public InvalidDepositAmountException() {
    super("Minimum deposit amount not reached");
  }
}
