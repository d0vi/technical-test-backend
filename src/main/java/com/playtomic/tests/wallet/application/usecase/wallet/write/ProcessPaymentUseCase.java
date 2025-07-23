package com.playtomic.tests.wallet.application.usecase.wallet.write;

import com.playtomic.tests.wallet.domain.model.transaction.Transaction;
import com.playtomic.tests.wallet.domain.model.transaction.TransactionRepository;
import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public class ProcessPaymentUseCase {

  private final WalletRepository walletRepository;
  private final TransactionRepository transactionRepository;
  private final DomainEventBus eventPublisher;

  public ProcessPaymentUseCase(
      WalletRepository walletRepository,
      TransactionRepository transactionRepository,
      DomainEventBus eventBus) {
    this.walletRepository = walletRepository;
    this.transactionRepository = transactionRepository;
    this.eventPublisher = eventBus;
  }

  @Transactional
  public void execute(UUID walletId, String paymentId, BigDecimal amount) {
    Wallet wallet =
        this.walletRepository
            .findById(walletId)
            .orElseThrow(() -> new UnknownWalletIdException(walletId));

    BigDecimal previousBalance = wallet.balance();
    wallet.deposit(amount);
    this.walletRepository.save(wallet);
    this.transactionRepository.save(new Transaction(walletId.toString(), amount, paymentId));

    this.eventPublisher.publishDomainEvent(
        new WalletToppedUp(walletId, amount, previousBalance, wallet.balance(), wallet.currency()));
  }
}
