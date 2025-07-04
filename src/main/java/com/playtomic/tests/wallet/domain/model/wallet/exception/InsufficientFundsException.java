package com.playtomic.tests.wallet.domain.model.wallet.exception;

import com.playtomic.tests.wallet.domain.model.wallet.vo.Balance;
import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

  public InsufficientFundsException(Balance balance, BigDecimal requested) {
    super(
        "Your wallet currently has "
            + balance
            + ", your request to withdraw "
            + requested
            + " is not valid");
  }
}
