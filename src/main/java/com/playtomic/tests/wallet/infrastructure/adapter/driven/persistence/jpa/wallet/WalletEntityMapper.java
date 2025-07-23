package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.wallet;

import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import java.sql.Timestamp;
import java.util.Optional;

public class WalletEntityMapper {

  public WalletEntity toEntity(Wallet wallet) {
    return new WalletEntity(
        wallet.id(),
        wallet.balance(),
        wallet.currency(),
        Timestamp.valueOf(wallet.createdAt()),
        Optional.ofNullable(wallet.updatedAt()).map(Timestamp::valueOf).orElse(null),
        Optional.ofNullable(wallet.deletedAt()).map(Timestamp::valueOf).orElse(null),
        wallet.version());
  }

  public Wallet toDomain(WalletEntity wallet) {
    return new Wallet(
        wallet.getId().toString(),
        wallet.getBalance(),
        wallet.getCurrency(),
        wallet.getVersion(),
        wallet.getCreatedAt().toLocalDateTime(),
        Optional.ofNullable(wallet.getUpdatedAt()).map(Timestamp::toLocalDateTime).orElse(null),
        Optional.ofNullable(wallet.getDeletedAt()).map(Timestamp::toLocalDateTime).orElse(null));
  }
}
