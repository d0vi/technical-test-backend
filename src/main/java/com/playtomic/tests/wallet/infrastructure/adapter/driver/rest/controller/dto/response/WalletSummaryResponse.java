package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletSummaryResponse(UUID id, BigDecimal balance, String currency) {}
