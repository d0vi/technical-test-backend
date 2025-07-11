package com.playtomic.tests.wallet.domain.model.wallet.vo;

public record Currency(ISO4217CurrencyCode currencyCode) {

  public Currency(String currencyCode) {
    this(ISO4217CurrencyCode.fromString(currencyCode));
  }
}
