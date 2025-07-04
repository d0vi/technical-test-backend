package com.playtomic.tests.wallet.domain.model.wallet.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidBalanceException;
import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidDepositAmountException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

class BalanceTest {

  @Test
  @DisplayName("should create a balance")
  void should_create_a_balance() {
    BigDecimal amount = new BigDecimal("100.00");

    Balance balance = new Balance(amount);

    assertThat(balance.amount()).isEqualTo(amount);
  }

  @Test
  @DisplayName("should round amount up")
  void should_round_amount_up() {
    BigDecimal amount = new BigDecimal("100.125");

    Balance balance = new Balance(amount);

    assertThat(balance.amount())
        .isEqualTo(new BigDecimal("100.13").setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  @DisplayName("should throw an exception when amount is null")
  void should_throw_an_exception_when_amount_is_null() {
    assertThatThrownBy(() -> new Balance(null))
        .isInstanceOf(InvalidBalanceException.class)
        .hasMessage("Wallet balance cannot be null");
  }

  @Test
  @DisplayName("should add amounts correctly")
  void should_add_amounts_correctly() {
    Balance balance = new Balance(new BigDecimal("100.00"));

    Balance result = balance.add(new BigDecimal("50.00"));

    assertThat(result.amount()).isEqualTo(new BigDecimal("150.00"));
  }

  @Test
  @DisplayName("should subtract amounts correctly")
  void should_subtract_amounts_correctly() {
    Balance balance = new Balance(new BigDecimal("100.00"));

    Balance result = balance.subtract(new BigDecimal("30.00"));

    assertThat(result.amount()).isEqualTo(new BigDecimal("70.00"));
  }

  @Test
  @DisplayName("should format toString with currency")
  void should_format_to_string_with_currency() {
    Balance balance = new Balance(new BigDecimal("100.50"));

    String result = balance.toString();

    assertThat(result).isEqualTo("100.50 EUR");
  }

  @ParameterizedTest
  @NullSource
  @MethodSource("provideAmounts")
  @DisplayName("should throw an exception when adding an invalid amount")
  void should_throw_exception_when_adding_invalid_amount(BigDecimal amount) {
    Balance balance = new Balance(new BigDecimal("100.50"));

    assertThatThrownBy(() -> balance.add(amount))
        .isInstanceOf(InvalidDepositAmountException.class)
        .hasMessage("Minimum deposit amount not reached");
    assertThat(balance.amount()).isEqualTo("100.50");
  }

  private static Stream<Arguments> provideAmounts() {
    return Stream.of(Arguments.of(BigDecimal.ZERO), Arguments.of(BigDecimal.valueOf(-50.00)));
  }
}
