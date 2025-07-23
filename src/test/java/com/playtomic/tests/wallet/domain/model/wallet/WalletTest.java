package com.playtomic.tests.wallet.domain.model.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WalletTest {

  private static final String CURRENCY_EUR = "EUR";

  @Test
  @DisplayName("should create a wallet with reduced constructor")
  void should_create_a_wallet_with_reduced_constructor() {
    Wallet wallet = new Wallet(CURRENCY_EUR);

    assertThat(wallet).isNotNull();
    assertThat(wallet.id()).isNotNull();
    assertThat(wallet.balance()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    assertThat(wallet.version()).isNull();
    assertThat(wallet.createdAt()).isBefore(LocalDateTime.now());
    assertThat(wallet.updatedAt()).isNull();
    assertThat(wallet.deletedAt()).isNull();
  }

  @Test
  @DisplayName("should create a wallet with all parameters")
  void should_create_a_wallet_with_all_parameters() {
    String walletIdString = "123e4567-e89b-12d3-a456-426614174000";
    BigDecimal balance = new BigDecimal("150.75");
    String currency = CURRENCY_EUR;
    Long version = 3L;
    LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
    LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);
    Wallet wallet =
        new Wallet(walletIdString, balance, currency, version, createdAt, updatedAt, null);

    assertThat(wallet).isNotNull();
    assertThat(wallet.id()).isEqualTo(UUID.fromString(walletIdString));
    assertThat(wallet.balance()).isEqualTo(balance.setScale(2, RoundingMode.HALF_UP));
    assertThat(wallet.currency()).isEqualTo(currency);
    assertThat(wallet.version()).isEqualTo(version);
    assertThat(wallet.createdAt()).isEqualTo(createdAt);
    assertThat(wallet.updatedAt()).isEqualTo(updatedAt);
    assertThat(wallet.deletedAt()).isNull();
  }

  @Test
  @DisplayName("should deposit an amount successfully")
  void should_deposit_amount_successfully() {
    Wallet wallet = new Wallet(CURRENCY_EUR);
    BigDecimal initialBalance = wallet.balance();
    BigDecimal depositAmount = new BigDecimal("100.50");
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";
    LocalDateTime beforeDeposit = LocalDateTime.now();

    wallet.deposit(depositAmount);

    assertThat(wallet.balance()).isEqualTo(initialBalance.add(depositAmount));
    assertThat(wallet.updatedAt()).isAfter(beforeDeposit);
  }
}
