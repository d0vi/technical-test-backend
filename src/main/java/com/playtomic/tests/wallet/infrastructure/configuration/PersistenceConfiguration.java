package com.playtomic.tests.wallet.infrastructure.configuration;

import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.WalletEntityMapper;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.WalletJpaRepository;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.WalletRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfiguration {

  @Bean
  public WalletEntityMapper walletEntityMapper() {
    return new WalletEntityMapper();
  }

  @Bean
  public WalletRepository walletRepository(
      final WalletJpaRepository walletJpaRepository, final WalletEntityMapper entityMapper) {
    return new WalletRepositoryImpl(walletJpaRepository, entityMapper);
  }
}
