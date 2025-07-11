package com.playtomic.tests.wallet.domain.model.wallet.exception;

public class InvalidCurrencyCodeException extends RuntimeException {

  public InvalidCurrencyCodeException() {
    super("You must provide a valid ISO 4217 currency code");
  }

  public InvalidCurrencyCodeException(String currencyCode) {
    super("'" + currencyCode + "' is not a valid ISO 4217 currency code");
  }
}
