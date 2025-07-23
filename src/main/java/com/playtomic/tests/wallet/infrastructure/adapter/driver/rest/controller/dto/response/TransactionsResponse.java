package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TransactionsResponse(
    List<TransactionResponse> transactions,
    int currentPage,
    int totalPages,
    long totalElements,
    boolean hasNext,
    boolean hasPrevious) {

  public record TransactionResponse(
      UUID id, String type, BigDecimal amount, String paymentId, LocalDateTime createdAt) {}
}
