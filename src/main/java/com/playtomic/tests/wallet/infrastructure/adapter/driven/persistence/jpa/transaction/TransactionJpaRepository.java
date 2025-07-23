package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa.transaction;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

  Page<TransactionEntity> findByWalletIdOrderByCreatedAtDesc(UUID walletId, Pageable pageable);
}
