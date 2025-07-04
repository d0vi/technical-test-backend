package com.playtomic.tests.wallet.infrastructure.adapter.driver.messaging.rabbit;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentProcessed;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
import com.playtomic.tests.wallet.infrastructure.configuration.MessagingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMQEventPublisher implements DomainEventBus {

  private static final Logger log = LoggerFactory.getLogger(RabbitMQEventPublisher.class);

  private final RabbitTemplate rabbitTemplate;

  public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void publishDomainEvent(Object event) {
    if (event instanceof WalletCreated walletCreated) {
      publishWalletCreated(walletCreated);
    } else if (event instanceof PaymentProcessed paymentProcessed) {
      publishPaymentProcessed(paymentProcessed);
    } else if (event instanceof WalletToppedUp walletToppedUp) {
      publishWalletToppedUp(walletToppedUp);
    } else {
      log.warn("Unknown domain event type: {}", event.getClass().getSimpleName());
    }
  }

  private void publishWalletCreated(WalletCreated event) {
    try {
      rabbitTemplate.convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.new", event);
    } catch (Exception e) {
      log.error("Error publishing WalletCreated event", e);
    }
  }

  private void publishPaymentProcessed(PaymentProcessed event) {
    try {
      rabbitTemplate.convertAndSend(
          MessagingConfiguration.PLAYTOMIC_EXCHANGE, "payment.new", event);
    } catch (Exception e) {
      log.error("Error publishing PaymentProcessed event", e);
    }
  }

  private void publishWalletToppedUp(WalletToppedUp event) {
    try {
      rabbitTemplate.convertAndSend(
          MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.top-up", event);
    } catch (Exception e) {
      log.error("Error publishing WalletRefunded event", e);
    }
  }
}
