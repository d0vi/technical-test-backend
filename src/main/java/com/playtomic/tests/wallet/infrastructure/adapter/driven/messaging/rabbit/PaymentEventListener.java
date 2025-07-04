package com.playtomic.tests.wallet.infrastructure.adapter.driven.messaging.rabbit;

import com.playtomic.tests.wallet.application.usecase.wallet.write.ProcessPaymentUseCase;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentProcessed;
import com.playtomic.tests.wallet.infrastructure.configuration.MessagingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

  private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);

  private final ProcessPaymentUseCase processPaymentUseCase;

  public PaymentEventListener(ProcessPaymentUseCase processPaymentUseCase) {
    this.processPaymentUseCase = processPaymentUseCase;
  }

  @RabbitListener(queues = MessagingConfiguration.PAYMENT_EVENTS_QUEUE)
  public void handlePaymentProcessed(PaymentProcessed event) {
    logger.info(
        "Processing payment event: walletId={}, paymentId={}, amount={}",
        event.walletId(),
        event.paymentId(),
        event.amount());

    try {
      processPaymentUseCase.execute(event.walletId(), event.paymentId(), event.amount());
      logger.info("Successfully processed payment for wallet: {}", event.walletId());
    } catch (Exception e) {
      logger.error(
          "Failed to process payment for wallet: {} - {}", event.walletId(), e.getMessage());
      // Exception will cause message to be requeued for retry
      throw e;
    }
  }
}
