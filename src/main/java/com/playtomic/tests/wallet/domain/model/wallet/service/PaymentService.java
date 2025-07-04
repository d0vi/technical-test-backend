package com.playtomic.tests.wallet.domain.model.wallet.service;

import java.math.BigDecimal;

public interface PaymentService {

  String charge(String creditCardNumber, BigDecimal amount);

  void refund(String paymentId);
}
