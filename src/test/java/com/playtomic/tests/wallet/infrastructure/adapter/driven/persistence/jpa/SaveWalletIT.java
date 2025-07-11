package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class SaveWalletIT extends WalletApplicationIT {

  private static final String CURRENCY_EUR = "EUR";

  @Autowired private WalletRepository walletRepository;

  @Test
  @Sql(scripts = "/sql/truncate_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @DisplayName("should save a wallet")
  void should_save_a_wallet() {
    Wallet wallet = this.walletRepository.save(new Wallet(CURRENCY_EUR));

    assertThat(wallet).isNotNull();
    assertThat(wallet.id()).isNotNull();
    assertThat(wallet.balance()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(wallet.createdAt()).isNotNull();
    assertThat(wallet.updatedAt()).isNull();
    assertThat(wallet.deletedAt()).isNull();
  }

  @Test
  @Sql(scripts = "/sql/truncate_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @DisplayName("should save a wallet with transactions")
  void should_save_a_wallet_with_transactions() {
    Wallet wallet = new Wallet(CURRENCY_EUR);
    wallet.deposit(new BigDecimal("50.00"), UUID.randomUUID().toString());
    wallet.deposit(new BigDecimal("25.00"), UUID.randomUUID().toString());

    Wallet savedWallet = this.walletRepository.save(wallet);

    assertThat(savedWallet.transactions()).hasSize(2);
    assertThat(savedWallet.balance()).isEqualByComparingTo(new BigDecimal("75.00"));
    assertThat(savedWallet.transactions()).hasSize(2);
    assertThat(savedWallet.balance()).isEqualByComparingTo(new BigDecimal("75.00"));
  }
}
