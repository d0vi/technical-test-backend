package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.helper.WalletTestBuilder;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class UpdateWalletIT extends WalletApplicationIT {

  @Autowired private WalletRepository walletRepository;

  @Test
  @Sql(scripts = "/sql/truncate_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @DisplayName("should update a wallet")
  void should_update_a_wallet() {
    Wallet wallet =
        this.walletRepository.save(
            new WalletTestBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withBalance("50.00")
                .build());
    assertThat(wallet.updatedAt()).isNull();

    wallet.deposit(new BigDecimal("100.00"));
    Wallet updatedWallet = this.walletRepository.save(wallet);

    assertThat(updatedWallet.id()).isEqualTo(wallet.id());
    assertThat(updatedWallet.balance()).isEqualByComparingTo(new BigDecimal("150.00"));
    assertThat(updatedWallet.updatedAt()).isNotNull();
    assertThat(updatedWallet.createdAt()).isEqualTo(wallet.createdAt());
  }
}
