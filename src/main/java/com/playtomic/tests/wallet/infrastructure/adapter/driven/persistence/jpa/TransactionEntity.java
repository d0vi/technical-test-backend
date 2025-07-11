package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import com.playtomic.tests.wallet.domain.model.wallet.vo.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class TransactionEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "wallet_id", nullable = false)
  private UUID walletId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private TransactionType type;

  @Column(name = "amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "payment_id", nullable = false, length = 50)
  private String paymentId;

  @Column(name = "created_at", nullable = false)
  private Timestamp createdAt;
}
