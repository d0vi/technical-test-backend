package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TopUpRequest(@NotBlank String creditCard, @NotNull @Positive BigDecimal amount) {}
