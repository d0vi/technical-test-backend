package com.playtomic.tests.wallet.infrastructure.adapter.driven.messaging.producer;

import static org.mockito.Mockito.verify;

import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentProcessed;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
import com.playtomic.tests.wallet.infrastructure.configuration.MessagingConfiguration;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class RabbitMQEventPublisherTest {

  @Mock private RabbitTemplate rabbitTemplate;

  @InjectMocks private RabbitMQEventPublisher eventPublisher;

  @Test
  @DisplayName("should publish a wallet created event to correct routing key")
  void should_publish_a_wallet_created_event_to_correct_routing_key() {
    WalletCreated event = new WalletCreated(UUID.randomUUID(), "EUR");

    this.eventPublisher.publishDomainEvent(event);

    verify(this.rabbitTemplate)
        .convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.new", event);
  }

  @Test
  @DisplayName("should publish a payment processed event to correct routing key")
  void should_publish_a_payment_processed_event_to_correct_routing_key() {
    PaymentProcessed event =
        new PaymentProcessed(
            UUID.randomUUID(),
            UUID.randomUUID().toString(),
            new BigDecimal("100.00"),
            "credit_card");

    this.eventPublisher.publishDomainEvent(event);

    verify(this.rabbitTemplate)
        .convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "payment.new", event);
  }

  @Test
  @DisplayName("should publish a wallet topped up event to correct routing key")
  void should_publish_a_wallet_topped_up_event_to_correct_routing_key() {
    WalletToppedUp event =
        new WalletToppedUp(
            UUID.randomUUID(), new BigDecimal("50.00"), BigDecimal.ZERO, new BigDecimal("50.00"));

    this.eventPublisher.publishDomainEvent(event);

    verify(this.rabbitTemplate)
        .convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.top-up", event);
  }
}
