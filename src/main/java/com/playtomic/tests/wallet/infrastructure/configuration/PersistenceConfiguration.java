package com.playtomic.tests.wallet.infrastructure.configuration;

import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.transaction.TransactionEntityMapper;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.transaction.TransactionJpaRepository;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.transaction.TransactionRepositoryImpl;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.wallet.WalletEntityMapper;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.wallet.WalletJpaRepository;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.wallet.WalletRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfiguration {

  @Bean
  public WalletEntityMapper walletEntityMapper() {
    return new WalletEntityMapper();
  }

  @Bean
  public TransactionEntityMapper transactionEntityMapper() {
    return new TransactionEntityMapper();
  }

  @Bean
  public WalletRepository walletRepository(
      final WalletJpaRepository walletJpaRepository, final WalletEntityMapper entityMapper) {
    return new WalletRepositoryImpl(walletJpaRepository, entityMapper);
  }

  @Bean
  public TransactionRepositoryImpl transactionRepository(
      final TransactionJpaRepository transactionJpaRepository,
      final TransactionEntityMapper entityMapper) {
    return new TransactionRepositoryImpl(transactionJpaRepository, entityMapper);
  }
}
