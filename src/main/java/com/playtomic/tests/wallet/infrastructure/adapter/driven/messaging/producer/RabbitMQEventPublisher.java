package com.playtomic.tests.wallet.infrastructure.adapter.driven.messaging.producer;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.event.Event;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentCreated;
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
  public void publishDomainEvent(Event event) {
    switch (event) {
      case WalletCreated walletCreated -> publishWalletCreated(walletCreated);
      case PaymentCreated paymentCreated -> publishPaymentProcessed(paymentCreated);
      case WalletToppedUp walletToppedUp -> publishWalletToppedUp(walletToppedUp);
      default -> log.warn("Unknown domain event type: {}", event.getClass().getSimpleName());
    }
  }

  private void publishWalletCreated(WalletCreated event) {
    try {
      rabbitTemplate.convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.new", event);
    } catch (Exception e) {
      log.error("Error publishing WalletCreated event", e);
    }
  }

  private void publishPaymentProcessed(PaymentCreated event) {
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
