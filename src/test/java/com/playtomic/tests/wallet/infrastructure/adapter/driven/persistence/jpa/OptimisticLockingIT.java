package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.application.usecase.wallet.write.ProcessPaymentUseCase;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

class OptimisticLockingIT extends WalletApplicationIT {

  @Autowired private WalletRepository walletRepository;
  @Autowired private ProcessPaymentUseCase processPaymentUseCase;

  @Test
  @Sql(scripts = "/sql/truncate_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @DisplayName("should retry and eventually succeed on concurrent wallet updates")
  void should_retry_and_eventually_succeed_on_concurrent_wallet_updates() throws Exception {
    Wallet wallet = walletRepository.save(new Wallet("EUR"));
    UUID walletId = wallet.id();
    BigDecimal amount = new BigDecimal("10.00");

    AtomicInteger optimisticLockFailures = new AtomicInteger(0);
    AtomicInteger successfulOperations = new AtomicInteger(0);

    int OPERATIONS = 100;
    // execute 100 concurrent deposit operations
    List<CompletableFuture<Void>> depositOperations =
        Stream.generate(
                () ->
                    CompletableFuture.runAsync(
                        () -> {
                          try {
                            processPaymentUseCase.execute(
                                walletId, UUID.randomUUID().toString(), amount);
                            successfulOperations.incrementAndGet();
                          } catch (OptimisticLockingFailureException e) {
                            // all retries were exhausted
                            optimisticLockFailures.incrementAndGet();
                          }
                        }))
            .limit(OPERATIONS)
            .toList();
    CompletableFuture.allOf(depositOperations.toArray(new CompletableFuture[0])).join();

    wallet = walletRepository.findById(walletId).orElseThrow();

    assertThat(successfulOperations.get())
        .as("All operations should succeed with retry mechanism")
        .isEqualTo(OPERATIONS);

    assertThat(optimisticLockFailures.get()).as("No failures expected").isEqualTo(0);

    assertThat(wallet.version())
        .as("Wallet version should equal number of successful deposits")
        .isEqualTo(successfulOperations.get());

    BigDecimal expectedBalance = amount.multiply(new BigDecimal(successfulOperations.get()));
    assertThat(wallet.balance())
        .as("Balance should equal amount × successful operations")
        .isEqualTo(expectedBalance);
  }
}
