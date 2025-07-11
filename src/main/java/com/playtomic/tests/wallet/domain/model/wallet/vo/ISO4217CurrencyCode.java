package com.playtomic.tests.wallet.domain.model.wallet.vo;

import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidCurrencyCodeException;
import java.util.Arrays;

public enum ISO4217CurrencyCode {
  EUR,
  USD,
  JPY,
  GBP,
  CAD;

  public static ISO4217CurrencyCode fromString(String str) {
    if (str == null) {
      throw new InvalidCurrencyCodeException();
    }
    return Arrays.stream(values())
        .filter(cc -> cc.name().equals(str))
        .findFirst()
        .orElseThrow(() -> new InvalidCurrencyCodeException(str));
  }
}
