package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidAuditStateException extends RuntimeException {

  public InvalidAuditStateException(String message) {
    super(message);
  }
}
