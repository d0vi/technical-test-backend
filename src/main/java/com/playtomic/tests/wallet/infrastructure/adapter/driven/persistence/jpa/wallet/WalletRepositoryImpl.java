package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.wallet;

import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public class WalletRepositoryImpl implements WalletRepository {

  private final WalletJpaRepository walletRepository;
  private final WalletEntityMapper mapper;

  public WalletRepositoryImpl(WalletJpaRepository walletRepository, WalletEntityMapper mapper) {
    this.walletRepository = walletRepository;
    this.mapper = mapper;
  }

  @Override
  public Optional<Wallet> findById(UUID id) {
    return walletRepository.findById(id).map(mapper::toDomain);
  }

  @Retryable(
      retryFor = {OptimisticLockingFailureException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100))
  @Transactional
  @Override
  public Wallet save(Wallet wallet) {
    WalletEntity entity = this.walletRepository.save(this.mapper.toEntity(wallet));
    return this.mapper.toDomain(entity);
  }
}
