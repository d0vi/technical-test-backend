package com.playtomic.tests.wallet.domain.model.wallet.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidWalletIdException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class WalletIdTest {

  @Test
  @DisplayName("should create a wallet id")
  void should_create_a_wallet_id() {
    UUID uuid = UUID.randomUUID();

    WalletId walletId = new WalletId(uuid);

    assertThat(walletId.uuid()).isEqualTo(uuid);
  }

  @Test
  @DisplayName("should create wallet id from string")
  void should_create_a_wallet_id_from_string() {
    String validUuidString = "123e4567-e89b-12d3-a456-426614174000";

    WalletId walletId = new WalletId(validUuidString);

    assertThat(walletId.uuid()).isEqualTo(UUID.fromString(validUuidString));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("should throw exception for invalid wallet id")
  void should_throw_exception_for_invalid_wallet_id(String invalidId) {
    assertThatThrownBy(() -> new WalletId(invalidId)).isInstanceOf(InvalidWalletIdException.class);
  }
}
