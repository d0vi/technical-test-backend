package com.playtomic.tests.wallet.domain.model.wallet.event;

public sealed interface Event
    permits PaymentCreated, PaymentRefunded, WalletCreated, WalletToppedUp {}
