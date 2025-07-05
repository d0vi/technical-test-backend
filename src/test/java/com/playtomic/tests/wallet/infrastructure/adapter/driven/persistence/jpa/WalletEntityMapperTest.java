package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.playtomic.tests.wallet.domain.model.wallet.Transaction;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.vo.TransactionType;
import com.playtomic.tests.wallet.helper.TransactionTestBuilder;
import com.playtomic.tests.wallet.helper.WalletTestBuilder;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WalletEntityMapperTest {

  private final WalletEntityMapper mapper = new WalletEntityMapper();

  @Test
  @DisplayName("should map wallet to entity")
  void should_map_wallet_to_entity() {
    UUID walletId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
    LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);
    LocalDateTime deletedAt = LocalDateTime.now();
    Transaction transaction =
        new TransactionTestBuilder()
            .withWalletId(walletId)
            .withAmount(new BigDecimal("100.50"))
            .build();
    Wallet wallet =
        new WalletTestBuilder()
            .withId(walletId)
            .withBalance("100.50")
            .withVersion(5L)
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt)
            .withDeletedAt(deletedAt)
            .withTransaction(transaction)
            .build();

    WalletEntity entity = mapper.toEntity(wallet);

    assertThat(entity.getId()).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    assertThat(entity.getBalance()).isEqualByComparingTo(new BigDecimal("100.50"));
    assertThat(entity.getVersion()).isEqualTo(5L);
    assertThat(entity.getCreatedAt().toLocalDateTime()).isEqualTo(createdAt);
    assertThat(entity.getUpdatedAt().toLocalDateTime()).isEqualTo(updatedAt);
    assertThat(entity.getDeletedAt().toLocalDateTime()).isEqualTo(deletedAt);
    assertThat(entity.getTransactions()).hasSize(1);
    TransactionEntity transactionEntity = entity.getTransactions().getFirst();
    assertThat(transactionEntity.getId()).isEqualTo(transaction.id());
    assertThat(transactionEntity.getWalletId()).isEqualTo(walletId);
    assertThat(transactionEntity.getType()).isEqualTo(TransactionType.DEPOSIT);
    assertThat(transactionEntity.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
    assertThat(transactionEntity.getPaymentId()).isEqualTo(transaction.paymentId());
  }

  @Test
  @DisplayName("should map entity to domain")
  void should_map_entity_to_domain() {
    UUID walletId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    WalletEntity entity = new WalletEntity();
    entity.setId(walletId);
    entity.setBalance(new BigDecimal("75.25"));
    entity.setVersion(3L);
    entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusHours(2)));
    entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
    TransactionEntity transactionEntity =
        new TransactionEntity(
            UUID.randomUUID(),
            walletId,
            TransactionType.DEPOSIT,
            new BigDecimal("75.25"),
            UUID.randomUUID().toString(),
            Timestamp.valueOf(LocalDateTime.now().minusHours(1)));
    entity.setTransactions(List.of(transactionEntity));

    Wallet wallet = mapper.toDomain(entity);

    assertThat(wallet.id()).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    assertThat(wallet.balance()).isEqualByComparingTo(new BigDecimal("75.25"));
    assertThat(wallet.version()).isEqualTo(3L);
    assertThat(wallet.createdAt()).isEqualTo(entity.getCreatedAt().toLocalDateTime());
    assertThat(wallet.updatedAt()).isEqualTo(entity.getUpdatedAt().toLocalDateTime());
    assertThat(wallet.deletedAt()).isNull();
  }

  @Test
  @DisplayName("should handle null timestamps when mapping to entity")
  void should_handle_null_timestamps_when_mapping_to_entity() {
    Wallet wallet =
        new WalletTestBuilder()
            .withId("123e4567-e89b-12d3-a456-426614174000")
            .withBalance("50.00")
            .build();

    WalletEntity entity = mapper.toEntity(wallet);

    assertThat(entity.getCreatedAt()).isNotNull();
    assertThat(entity.getUpdatedAt()).isNull();
    assertThat(entity.getDeletedAt()).isNull();
  }
}
