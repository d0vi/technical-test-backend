package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

public record WalletSummaryResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("balance") BigDecimal balance,
    @JsonProperty("currency") String currency) {}
