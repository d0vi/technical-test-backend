package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateWalletRequest(@JsonProperty("currency_code") @NotBlank String currencyCode) {}
