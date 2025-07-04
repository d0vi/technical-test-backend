package com.playtomic.tests.wallet.application.usecase.wallet.write;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import java.math.BigDecimal;
import java.util.UUID;

public class ProcessPaymentUseCase {

  private final WalletRepository repository;
  private final DomainEventBus eventPublisher;

  public ProcessPaymentUseCase(WalletRepository repository, DomainEventBus eventBus) {
    this.repository = repository;
    this.eventPublisher = eventBus;
  }

  public void execute(UUID walletId, String paymentId, BigDecimal amount) {
    Wallet wallet =
        this.repository
            .findById(walletId)
            .orElseThrow(() -> new UnknownWalletIdException(walletId));

    BigDecimal previousBalance = wallet.balance();
    wallet.deposit(amount, paymentId);
    this.repository.save(wallet);

    this.eventPublisher.publishDomainEvent(
        new WalletToppedUp(walletId, amount, previousBalance, wallet.balance()));
  }
}
