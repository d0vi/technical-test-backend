package com.playtomic.tests.wallet.application.usecase.wallet.write;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletCreated;

public class CreateWalletUseCase {

  private final WalletRepository repository;
  private final DomainEventBus eventPublisher;

  public CreateWalletUseCase(WalletRepository repository, DomainEventBus eventBus) {
    this.repository = repository;
    this.eventPublisher = eventBus;
  }

  public Wallet execute() {
    Wallet wallet = new Wallet();
    this.eventPublisher.publishDomainEvent(new WalletCreated(wallet.id()));
    return this.repository.save(wallet);
  }
}
