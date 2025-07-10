package com.playtomic.tests.wallet.infrastructure.adapter.driven.messaging.producer;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
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

  @Autowired private DomainEventBus domainEventBus;

  @MockitoSpyBean private RabbitMQEventListener rabbitMQEventListener;

  @Test
  @DisplayName("should publish a wallet topped up event")
  void should_publish_a_wallet_topped_up_event() {
    UUID walletId = UUID.randomUUID();
    WalletToppedUp event =
        new WalletToppedUp(
            walletId, new BigDecimal("50.00"), BigDecimal.ZERO, new BigDecimal("50.00"));

    this.domainEventBus.publishDomainEvent(event);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () ->
                verify(this.rabbitMQEventListener, atLeastOnce())
                    .handleWalletEvents(any(WalletToppedUp.class)));
  }
}
