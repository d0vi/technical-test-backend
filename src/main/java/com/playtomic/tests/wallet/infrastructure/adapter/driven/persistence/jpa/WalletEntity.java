package com.playtomic.tests.wallet.infrastructure.adapter.driven.persistence.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "wallet")
public class WalletEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "balance", nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  @Column(name = "created_at", nullable = false)
  private Timestamp createdAt;

  @Column(name = "updated_at")
  private Timestamp updatedAt;

  @Column(name = "deleted_at")
  private Timestamp deletedAt;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "wallet_id")
  private List<TransactionEntity> transactions = new ArrayList<>();
}
