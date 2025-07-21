package com.playtomic.tests.wallet.domain.model.wallet.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreated(UUID walletId, String paymentId, BigDecimal amount) implements Event {}
