package com.playtomic.tests.wallet.domain.model.wallet.event;

import java.util.UUID;

public record WalletCreated(UUID walletId) implements Event {}
