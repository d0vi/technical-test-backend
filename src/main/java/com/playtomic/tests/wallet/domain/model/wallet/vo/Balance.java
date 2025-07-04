package com.playtomic.tests.wallet.domain.model.wallet.vo;

import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidBalanceException;
import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidDepositAmountException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Balance(BigDecimal amount) {

  private static final BigDecimal MINIMUM_DEPOSIT = BigDecimal.TEN;

  public static final String CURRENCY = "EUR";

  public Balance {
    if (amount == null) {
      throw new InvalidBalanceException();
    }
    amount = amount.setScale(2, RoundingMode.HALF_UP);
  }

  public Balance add(BigDecimal amount) {
    if (amount == null || amount.compareTo(MINIMUM_DEPOSIT) < 0) {
      throw new InvalidDepositAmountException();
    }
    return new Balance(this.amount.add(amount));
  }

  public Balance subtract(BigDecimal amount) {
    return new Balance(this.amount.subtract(amount));
  }

  @Override
  public String toString() {
    return amount + " " + CURRENCY;
  }
}
