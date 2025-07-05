package com.playtomic.tests.wallet.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class WalletPersistenceIT extends WalletApplicationIT {

  @Autowired private WalletRepository walletRepository;

  @Test
  @Sql(scripts = "/sql/truncate_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @DisplayName("should save wallet")
  void should_save_wallet() {
    Wallet wallet = walletRepository.save(new Wallet());

    assertThat(wallet).isNotNull();
    assertThat(wallet.id()).isNotNull();
    assertThat(wallet.balance()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(wallet.createdAt()).isNotNull();
    assertThat(wallet.updatedAt()).isNull();
    assertThat(wallet.deletedAt()).isNull();
  }

  @Test
  @Sql(scripts = "/sql/insert_wallet.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/truncate_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @DisplayName("should find wallet")
  void should_find_wallet() {
    UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    Optional<Wallet> wallet = walletRepository.findById(id);
    assertThat(wallet).isPresent();
    assertThat(wallet.get().id()).isEqualTo(id);
  }
}
