package com.playtomic.tests.wallet.domain.model.wallet.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessed(
    UUID walletId, String paymentId, BigDecimal amount, String paymentMethod) implements Event {}
