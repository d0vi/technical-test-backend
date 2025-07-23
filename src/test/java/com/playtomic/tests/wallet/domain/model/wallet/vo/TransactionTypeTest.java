package com.playtomic.tests.wallet.domain.model.wallet.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.domain.model.transaction.vo.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransactionTypeTest {

  @Test
  @DisplayName("should have all expected transaction types")
  void should_have_all_expected_transaction_types() {
    TransactionType[] values = TransactionType.values();

    assertThat(values).hasSize(3);
    assertThat(values)
        .containsExactlyInAnyOrder(
            TransactionType.DEPOSIT, TransactionType.PURCHASE, TransactionType.REFUND);
  }
}
