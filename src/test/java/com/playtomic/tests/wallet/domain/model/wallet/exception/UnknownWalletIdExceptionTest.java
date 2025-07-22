package com.playtomic.tests.wallet.domain.model.wallet.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnknownWalletIdExceptionTest {

  @Test
  @DisplayName("should create an exception with the wallet id")
  void should_create_an_exception_with_the_wallet_id() {
    UUID walletId = UUID.randomUUID();

    UnknownWalletIdException exception = new UnknownWalletIdException(walletId);

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isEqualTo("Wallet " + walletId + " can not be found");
  }
}
