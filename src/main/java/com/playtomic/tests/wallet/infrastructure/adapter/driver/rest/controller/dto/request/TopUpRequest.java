package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TopUpRequest(
    @JsonProperty("credit_card") @NotBlank String creditCard,
    @JsonProperty("amount") @NotNull @Positive BigDecimal amount) {}
