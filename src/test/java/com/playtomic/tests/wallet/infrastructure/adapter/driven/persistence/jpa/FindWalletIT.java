package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class FindWalletIT extends WalletApplicationIT {

  @Autowired private WalletRepository walletRepository;

  @Test
  @Sql(scripts = "/sql/insert_wallet.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/truncate_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @DisplayName("should find a wallet")
  void should_find_a_wallet() {
    UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    Optional<Wallet> wallet = this.walletRepository.findById(id);
    assertThat(wallet).isPresent();
    assertThat(wallet.get().id()).isEqualTo(id);
  }

  @Test
  @DisplayName("should return empty when wallet not found")
  void should_return_empty_when_wallet_not_found() {
    Optional<Wallet> wallet = this.walletRepository.findById(UUID.randomUUID());
    assertThat(wallet).isEmpty();
  }
}
