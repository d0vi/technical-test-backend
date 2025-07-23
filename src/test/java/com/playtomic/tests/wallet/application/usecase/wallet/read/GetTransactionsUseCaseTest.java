package com.playtomic.tests.wallet.application.usecase.wallet.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.playtomic.tests.wallet.application.usecase.transaction.read.GetTransactionsUseCase;
import com.playtomic.tests.wallet.domain.model.transaction.Page;
import com.playtomic.tests.wallet.domain.model.transaction.Transaction;
import com.playtomic.tests.wallet.domain.model.transaction.TransactionRepository;
import com.playtomic.tests.wallet.helper.TransactionTestBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetTransactionsUseCaseTest {

  @Mock private TransactionRepository transactionRepository;

  @InjectMocks private GetTransactionsUseCase getTransactionsUseCase;

  @Test
  @DisplayName("should return paginated transactions for a wallet")
  void should_return_paginated_transactions_for_a_wallet() {
    UUID walletId = UUID.randomUUID();
    int page = 0;
    int size = 10;
    Page<Transaction> expectedPage =
        new Page<>(
            List.of(
                new TransactionTestBuilder().withAmount(new BigDecimal("50.00")).build(),
                new TransactionTestBuilder().withAmount(new BigDecimal("25.00")).build()),
            0,
            1,
            2,
            false,
            false);
    when(transactionRepository.findTransactionsByWalletId(walletId, page, size))
        .thenReturn(expectedPage);

    Page<Transaction> result = this.getTransactionsUseCase.execute(walletId, page, size);

    assertThat(result).isNotNull();
    assertThat(result.content()).hasSize(2);
    assertThat(result.currentPage()).isEqualTo(0);
    assertThat(result.totalPages()).isEqualTo(1);
    assertThat(result.totalElements()).isEqualTo(2);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("should return empty page when wallet has no transactions")
  void should_return_empty_page_when_wallet_has_no_transactions() {
    UUID walletId = UUID.randomUUID();
    int page = 0;
    int size = 10;
    Page<Transaction> expectedPage = new Page<>(List.of(), 0, 0, 0, false, false);
    when(transactionRepository.findTransactionsByWalletId(walletId, page, size))
        .thenReturn(expectedPage);

    Page<Transaction> result = this.getTransactionsUseCase.execute(walletId, page, size);

    assertThat(result).isNotNull();
    assertThat(result.content()).isEmpty();
    assertThat(result.currentPage()).isEqualTo(0);
    assertThat(result.totalPages()).isEqualTo(0);
    assertThat(result.totalElements()).isEqualTo(0);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.hasPrevious()).isFalse();
  }
}
