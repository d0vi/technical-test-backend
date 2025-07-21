package com.playtomic.tests.wallet.application.usecase.wallet.write;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentRefunded;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import com.playtomic.tests.wallet.domain.model.wallet.service.PaymentService;
import java.math.BigDecimal;
import java.util.UUID;

public class RefundPaymentUseCase {

  private final WalletRepository repository;
  private final PaymentService paymentService;
  private final DomainEventBus eventPublisher;

  public RefundPaymentUseCase(
      WalletRepository repository, PaymentService paymentService, DomainEventBus eventBus) {
    this.repository = repository;
    this.paymentService = paymentService;
    this.eventPublisher = eventBus;
  }

  public void execute(UUID walletId, String paymentId, BigDecimal amount) {
    Wallet wallet =
        this.repository
            .findById(walletId)
            .orElseThrow(() -> new UnknownWalletIdException(walletId));

    this.paymentService.refund(paymentId);

    this.eventPublisher.publishDomainEvent(
        new PaymentRefunded(walletId, paymentId, amount, wallet.currency()));
  }
}
