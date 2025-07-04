package com.playtomic.tests.wallet.domain.model.wallet.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvalidAmountExceptionTest {

  @Test
  @DisplayName("should create an exception")
  void should_create_an_exception() {
    InvalidAmountException exception = new InvalidAmountException();

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isEqualTo("Deposit amount must be greater than 0");
  }
}
