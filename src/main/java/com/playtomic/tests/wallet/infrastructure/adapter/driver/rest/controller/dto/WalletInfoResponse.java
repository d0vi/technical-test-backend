package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WalletInfoResponse(
    UUID id, BigDecimal balance, String currency, List<TransactionResponse> transactions) {
  public record TransactionResponse(
      UUID id, String type, BigDecimal amount, String paymentId, LocalDateTime createdAt) {}
}
