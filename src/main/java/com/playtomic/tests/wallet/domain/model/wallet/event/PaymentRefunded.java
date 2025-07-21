package com.playtomic.tests.wallet.domain.model.wallet.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefunded(UUID walletId, String paymentId, BigDecimal amount, String currency)
    implements Event {}
