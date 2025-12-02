package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TransactionsResponse(
    @JsonProperty("transactions") List<TransactionResponse> transactions,
    @JsonProperty("current_page") int currentPage,
    @JsonProperty("total_pages") int totalPages,
    @JsonProperty("total_elements") long totalElements,
    @JsonProperty("has_next") boolean hasNext,
    @JsonProperty("has_previous") boolean hasPrevious) {

  public record TransactionResponse(
      @JsonProperty("id") UUID id,
      @JsonProperty("type") String type,
      @JsonProperty("amount") BigDecimal amount,
      @JsonProperty("payment_id") String paymentId,
      @JsonProperty("created_at") LocalDateTime createdAt) {}
}
