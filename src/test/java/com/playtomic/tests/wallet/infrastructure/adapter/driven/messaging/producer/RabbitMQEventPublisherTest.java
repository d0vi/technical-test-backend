package com.playtomic.tests.wallet.infrastructure.adapter.driven.messaging.producer;

import static org.mockito.Mockito.verify;

import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentRefunded;
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
  @DisplayName("should publish a wallet created event")
  void should_publish_a_wallet_created_event() {
    WalletCreated event = new WalletCreated(UUID.randomUUID(), "EUR");

    this.eventPublisher.publishDomainEvent(event);

    verify(this.rabbitTemplate)
        .convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.new", event);
  }

  @Test
  @DisplayName("should publish a payment processed event")
  void should_publish_a_payment_processed_event() {
    PaymentCreated event =
        new PaymentCreated(
            UUID.randomUUID(), UUID.randomUUID().toString(), new BigDecimal("100.00"));

    this.eventPublisher.publishDomainEvent(event);

    verify(this.rabbitTemplate)
        .convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "payment.new", event);
  }

  @Test
  @DisplayName("should publish a payment refunded event")
  void should_publish_a_payment_refunded_event() {
    PaymentRefunded event =
        new PaymentRefunded(
            UUID.randomUUID(), UUID.randomUUID().toString(), new BigDecimal("100.00"), "USD");

    this.eventPublisher.publishDomainEvent(event);

    verify(this.rabbitTemplate)
        .convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "payment.refunded", event);
  }

  @Test
  @DisplayName("should publish a wallet topped up event")
  void should_publish_a_wallet_topped_up_event() {
    WalletToppedUp event =
        new WalletToppedUp(
            UUID.randomUUID(), new BigDecimal("50.00"), BigDecimal.ZERO, new BigDecimal("50.00"));

    this.eventPublisher.publishDomainEvent(event);

    verify(this.rabbitTemplate)
        .convertAndSend(MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.top-up", event);
  }
}
