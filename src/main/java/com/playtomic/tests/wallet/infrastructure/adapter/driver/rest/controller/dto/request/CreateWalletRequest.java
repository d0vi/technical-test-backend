package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateWalletRequest(@NotBlank String currencyCode) {}
