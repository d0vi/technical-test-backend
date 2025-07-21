package com.playtomic.tests.wallet.infrastructure.adapter.driver.messaging.consumer;

import com.playtomic.tests.wallet.application.usecase.wallet.write.ProcessPaymentUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.RefundPaymentUseCase;
import com.playtomic.tests.wallet.domain.model.wallet.event.Event;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentRefunded;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
import com.playtomic.tests.wallet.infrastructure.configuration.MessagingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class RabbitMQEventListener {

  private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventListener.class);

  private final ProcessPaymentUseCase processPaymentUseCase;
  private final RefundPaymentUseCase refundPaymentUseCase;

  public RabbitMQEventListener(
      ProcessPaymentUseCase processPaymentUseCase, RefundPaymentUseCase refundPaymentUseCase) {
    this.processPaymentUseCase = processPaymentUseCase;
    this.refundPaymentUseCase = refundPaymentUseCase;
  }

  @RabbitListener(queues = MessagingConfiguration.WALLET_EVENTS_QUEUE)
  public void handleWalletEvents(Event event) {
    switch (event) {
      case WalletCreated wc ->
          logger.info("Wallet {} ({}) has been created", wc.walletId(), wc.currency());
      case WalletToppedUp wtu ->
          logger.info("Wallet {} has added {} EUR", wtu.walletId(), wtu.amount());
      default -> logger.warn("Unknown wallet event: {}", event.getClass().getSimpleName());
    }
  }

  @RabbitListener(queues = MessagingConfiguration.PAYMENT_EVENTS_QUEUE)
  public void handlePaymentEvents(Event event) {
    switch (event) {
      case PaymentCreated paymentCreated -> this.processPaymentCreated(paymentCreated);
      case PaymentRefunded paymentRefunded -> this.processPaymentRefunded(paymentRefunded);

      default -> logger.warn("Unknown payment event: {}", event.getClass().getSimpleName());
    }
  }

  private void processPaymentCreated(PaymentCreated event) {
    try {
      this.processPaymentUseCase.execute(event.walletId(), event.paymentId(), event.amount());
      logger.info("Successfully processed payment for wallet {}", event.walletId());
    } catch (Exception e) {
      logger.error("Wallet {} could not process payment: {}", event.walletId(), e.getMessage());
      // compensation logic: if the payment can not be processed, issue a refund
      this.refundPaymentUseCase.execute(event.walletId(), event.paymentId(), event.amount());
    }
  }

  private void processPaymentRefunded(PaymentRefunded event) {
    logger.info(
        "Wallet {} has been refunded {} {}", event.walletId(), event.amount(), event.currency());
  }
}
