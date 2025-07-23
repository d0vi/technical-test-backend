package com.playtomic.tests.wallet.domain.model.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.domain.model.transaction.Transaction;
import com.playtomic.tests.wallet.domain.model.transaction.vo.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransactionTest {

  @Test
  @DisplayName("should create a transaction with reduced constructor")
  void should_create_a_transaction_with_reduced_constructor() {
    UUID walletId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("100.00");
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";

    Transaction transaction = new Transaction(walletId.toString(), amount, paymentId);

    assertThat(transaction).isNotNull();
    assertThat(transaction.id()).isNotNull();
    assertThat(transaction.walletId()).isEqualTo(walletId);
    assertThat(transaction.type()).isEqualTo(TransactionType.DEPOSIT);
    assertThat(transaction.amount()).isEqualTo(amount);
    assertThat(transaction.paymentId()).isEqualTo(paymentId);
    assertThat(transaction.version()).isNull();
    assertThat(transaction.createdAt()).isBefore(LocalDateTime.now());
  }

  @Test
  @DisplayName("should create a transaction with all parameters")
  void should_create_a_transaction_with_all_parameters() {
    UUID id = UUID.randomUUID();
    UUID walletId = UUID.randomUUID();
    TransactionType type = TransactionType.DEPOSIT;
    BigDecimal amount = new BigDecimal("100.00");
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";
    Long version = 3L;
    LocalDateTime createdAt = LocalDateTime.now();

    Transaction transaction =
        new Transaction(
            id.toString(), walletId.toString(), type, amount, paymentId, version, createdAt);

    assertThat(transaction).isNotNull();
    assertThat(transaction.id()).isEqualTo(id);
    assertThat(transaction.walletId()).isEqualTo(walletId);
    assertThat(transaction.type()).isEqualTo(type);
    assertThat(transaction.amount()).isEqualTo(amount);
    assertThat(transaction.paymentId()).isEqualTo(paymentId);
    assertThat(transaction.version()).isEqualTo(version);
    assertThat(transaction.createdAt()).isEqualTo(createdAt);
  }
}
