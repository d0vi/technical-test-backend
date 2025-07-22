package com.playtomic.tests.wallet.domain.model.wallet.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvalidWalletIdExceptionTest {

  @Test
  @DisplayName("should create an exception")
  void should_create_an_exception() {
    InvalidWalletIdException exception = new InvalidWalletIdException();

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isEqualTo("Wallet id must not be null or empty");
  }

  @Test
  @DisplayName("should create an exception with the wallet id")
  void should_create_an_exception_with_the_wallet_id() {
    String invalidWalletId = UUID.randomUUID().toString();

    InvalidWalletIdException exception = new InvalidWalletIdException(invalidWalletId);

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isEqualTo(invalidWalletId + " is not a valid wallet id");
  }
}
