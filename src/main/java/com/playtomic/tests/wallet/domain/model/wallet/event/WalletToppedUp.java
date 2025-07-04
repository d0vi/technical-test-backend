package com.playtomic.tests.wallet.domain.model.wallet.event;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletToppedUp(
    UUID walletId, BigDecimal amount, BigDecimal previousBalance, BigDecimal newBalance)
    implements Event {}
