package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.wallet;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletJpaRepository extends JpaRepository<WalletEntity, UUID> {}
