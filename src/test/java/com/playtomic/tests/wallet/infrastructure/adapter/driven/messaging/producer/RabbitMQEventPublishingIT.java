package com.playtomic.tests.wallet.infrastructure.adapter.driven.messaging.producer;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentRefunded;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.messaging.consumer.RabbitMQEventListener;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class RabbitMQEventPublishingIT extends WalletApplicationIT {

  @Autowired private DomainEventBus eventBus;

  @MockitoSpyBean private RabbitMQEventListener rabbitMQEventListener;

  @Test
  @DisplayName("should publish a wallet topped up event")
  void should_publish_a_wallet_topped_up_event() {
    var event =
        new WalletToppedUp(
            UUID.randomUUID(),
            new BigDecimal("50.00"),
            BigDecimal.ZERO,
            new BigDecimal("50.00"),
            "USD");

    this.eventBus.publishDomainEvent(event);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> verify(this.rabbitMQEventListener, atLeastOnce()).handleWalletEvents(eq(event)));
  }

  @Test
  @DisplayName("should publish a payment refunded event")
  void should_publish_a_payment_refunded_event() {
    var event =
        new PaymentRefunded(
            UUID.randomUUID(), UUID.randomUUID().toString(), new BigDecimal("50.00"), "USD");

    this.eventBus.publishDomainEvent(event);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> verify(this.rabbitMQEventListener, atLeastOnce()).handlePaymentEvents(eq(event)));
  }
}
