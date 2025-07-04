package com.playtomic.tests.wallet.domain.model.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.domain.model.wallet.vo.TransactionType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WalletTest {

  @Test
  @DisplayName("should create a wallet")
  void should_create_a_wallet() {
    Wallet wallet = new Wallet();

    assertThat(wallet).isNotNull();
    assertThat(wallet.id()).isNotNull();
    assertThat(wallet.balance()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    assertThat(wallet.version()).isNull();
    assertThat(wallet.createdAt()).isBefore(LocalDateTime.now());
    assertThat(wallet.updatedAt()).isNull();
    assertThat(wallet.deletedAt()).isNull();
    assertThat(wallet.transactions()).isEmpty();
  }

  @Test
  @DisplayName("should create a wallet with all parameters")
  void should_create_a_wallet_with_all_parameters() {
    String walletIdString = "123e4567-e89b-12d3-a456-426614174000";
    BigDecimal balance = new BigDecimal("150.75");
    Long version = 3L;
    LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
    LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);
    List<Transaction> transactions =
        List.of(
            new Transaction(
                UUID.randomUUID().toString(),
                walletIdString,
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "d9183c7d-a682-47be-9817-96d3627539ee",
                createdAt.plusMinutes(10)),
            new Transaction(
                UUID.randomUUID().toString(),
                walletIdString,
                TransactionType.DEPOSIT,
                new BigDecimal("50.75"),
                "c4005a41-7054-4fc8-8c18-9034ee8e0760",
                updatedAt.minusMinutes(5)));

    Wallet wallet =
        new Wallet(walletIdString, balance, version, createdAt, updatedAt, null, transactions);

    assertThat(wallet.id()).isEqualTo(UUID.fromString(walletIdString));
    assertThat(wallet.balance()).isEqualTo(balance.setScale(2, RoundingMode.HALF_UP));
    assertThat(wallet.version()).isEqualTo(version);
    assertThat(wallet.createdAt()).isEqualTo(createdAt);
    assertThat(wallet.updatedAt()).isEqualTo(updatedAt);
    assertThat(wallet.deletedAt()).isNull();
    assertThat(wallet.transactions()).hasSize(2);
    assertThat(wallet.transactions())
        .isNotSameAs(transactions); // checks defensive copying of transactions
  }

  @Test
  @DisplayName("should deposit an amount successfully")
  void should_deposit_amount_successfully() {
    Wallet wallet = new Wallet();
    BigDecimal initialBalance = wallet.balance();
    BigDecimal depositAmount = new BigDecimal("100.50");
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";
    LocalDateTime beforeDeposit = LocalDateTime.now();

    wallet.deposit(depositAmount, paymentId);

    assertThat(wallet.balance()).isEqualTo(initialBalance.add(depositAmount));
    assertThat(wallet.updatedAt()).isAfter(beforeDeposit);
    assertThat(wallet.transactions()).hasSize(1);
    Transaction transaction = wallet.transactions().getFirst();
    assertThat(transaction.walletId()).isEqualTo(wallet.id());
    assertThat(transaction.amount()).isEqualTo(depositAmount);
    assertThat(transaction.paymentId()).isEqualTo(paymentId);
    assertThat(transaction.type()).isEqualTo(TransactionType.DEPOSIT);
    assertThat(transaction.createdAt()).isAfter(beforeDeposit);
  }
}
