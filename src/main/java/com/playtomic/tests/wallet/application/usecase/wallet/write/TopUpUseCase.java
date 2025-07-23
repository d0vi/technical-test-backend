package com.playtomic.tests.wallet.application.usecase.wallet.write;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentCreated;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import com.playtomic.tests.wallet.domain.model.wallet.service.PaymentService;
import java.math.BigDecimal;
import java.util.UUID;

public class TopUpUseCase {

  private final WalletRepository repository;
  private final PaymentService paymentService;
  private final DomainEventBus eventPublisher;

  public TopUpUseCase(
      WalletRepository repository, PaymentService paymentService, DomainEventBus eventBus) {
    this.repository = repository;
    this.paymentService = paymentService;
    this.eventPublisher = eventBus;
  }

  public String execute(UUID walletId, String creditCard, BigDecimal amount) {
    this.repository.findById(walletId).orElseThrow(() -> new UnknownWalletIdException(walletId));

    String paymentId = this.paymentService.charge(creditCard, amount);
    this.eventPublisher.publishDomainEvent(new PaymentCreated(walletId, paymentId, amount));
    return paymentId;
  }
}
