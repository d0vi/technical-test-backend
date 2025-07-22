package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidCurrencyCodeException extends RuntimeException {

  public InvalidCurrencyCodeException() {
    super("A valid ISO 4217 currency code is required");
  }

  public InvalidCurrencyCodeException(String currencyCode) {
    super(currencyCode + " is not a valid ISO 4217 currency code");
  }
}
