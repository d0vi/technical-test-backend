package com.playtomic.tests.wallet.infrastructure.adapter.driver.messaging.consumer;

import com.playtomic.tests.wallet.application.usecase.wallet.write.ProcessPaymentUseCase;
import com.playtomic.tests.wallet.domain.model.wallet.event.Event;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
import com.playtomic.tests.wallet.infrastructure.configuration.MessagingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class RabbitMQEventListener {

  private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventListener.class);

  private final ProcessPaymentUseCase processPaymentUseCase;

  public RabbitMQEventListener(ProcessPaymentUseCase processPaymentUseCase) {
    this.processPaymentUseCase = processPaymentUseCase;
  }

  @RabbitListener(queues = MessagingConfiguration.WALLET_EVENTS_QUEUE)
  public void handleWalletEvents(Event event) {
    switch (event) {
      case WalletCreated wc ->
          logger.info("Wallet {} ({}) has been created", wc.walletId(), wc.currency());
      case WalletToppedUp wtu ->
          logger.info("Wallet {} has added {} EUR", wtu.walletId(), wtu.amount());
      default -> logger.warn("Unknown domain event type: {}", event.getClass().getSimpleName());
    }
  }

  @RabbitListener(queues = MessagingConfiguration.PAYMENT_EVENTS_QUEUE)
  public void handlePaymentEvents(PaymentCreated event) {
    // currently just one kind of payment event
    try {
      processPaymentUseCase.execute(event.walletId(), event.paymentId(), event.amount());
      logger.info("Successfully processed payment for wallet {}", event.walletId());
    } catch (Exception e) {
      logger.error("Wallet {} could not process payment: {}", event.walletId(), e.getMessage());
      throw e; // rethrowing to trigger a retry
    }
  }
}
