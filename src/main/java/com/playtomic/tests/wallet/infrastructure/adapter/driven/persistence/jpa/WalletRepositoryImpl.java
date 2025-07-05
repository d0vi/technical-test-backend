package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

public class WalletRepositoryImpl implements WalletRepository {

  private final WalletJpaRepository repository;
  private final WalletEntityMapper mapper;

  public WalletRepositoryImpl(WalletJpaRepository repository, WalletEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Optional<Wallet> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Transactional
  @Override
  public Wallet save(Wallet wallet) {
    WalletEntity entity = this.repository.save(this.mapper.toEntity(wallet));
    return this.mapper.toDomain(entity);
  }
}
