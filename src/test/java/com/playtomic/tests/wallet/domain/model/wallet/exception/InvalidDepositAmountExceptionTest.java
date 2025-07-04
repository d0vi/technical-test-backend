package com.playtomic.tests.wallet.domain.model.wallet.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvalidDepositAmountExceptionTest {

  @Test
  @DisplayName("should create an exception")
  void should_create_an_exception() {
    InvalidDepositAmountException exception = new InvalidDepositAmountException();

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isEqualTo("Minimum deposit amount not reached");
  }
}
