package com.playtomic.tests.wallet.infrastructure.adapter.driver.messaging.consumer;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.application.usecase.wallet.write.CreateWalletUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.ProcessPaymentUseCase;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentRefunded;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletCreated;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
import com.playtomic.tests.wallet.infrastructure.configuration.MessagingConfiguration;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class RabbitMQEventListenerIT extends WalletApplicationIT {

  @Autowired private RabbitTemplate rabbitTemplate;

  @Autowired private RabbitAdmin rabbitAdmin;

  @Autowired private CreateWalletUseCase createWalletUseCase;

  @MockitoSpyBean private ProcessPaymentUseCase processPaymentUseCase;

  @Test
  @DisplayName("should handle wallet created event")
  void should_handle_a_wallet_created_event() {
    UUID walletId = UUID.randomUUID();
    WalletCreated event = new WalletCreated(walletId, "EUR");

    this.rabbitTemplate.convertAndSend(
        MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.new", event);

    await()
        .atMost(Duration.ofSeconds(3))
        .pollDelay(Duration.ofMillis(100))
        .until(
            () ->
                this.rabbitAdmin
                        .getQueueInfo(MessagingConfiguration.WALLET_EVENTS_QUEUE)
                        .getMessageCount()
                    == 0);
  }

  @Test
  @DisplayName("should handle a wallet topped up event")
  void should_handle_a_wallet_topped_up_event() {
    UUID walletId = UUID.randomUUID();
    WalletToppedUp event =
        new WalletToppedUp(
            walletId, new BigDecimal("100.00"), BigDecimal.ZERO, new BigDecimal("100.00"));

    this.rabbitTemplate.convertAndSend(
        MessagingConfiguration.PLAYTOMIC_EXCHANGE, "wallet.top-up", event);

    await()
        .atMost(Duration.ofSeconds(3))
        .pollDelay(Duration.ofMillis(100))
        .until(
            () ->
                this.rabbitAdmin
                        .getQueueInfo(MessagingConfiguration.WALLET_EVENTS_QUEUE)
                        .getMessageCount()
                    == 0);
  }

  @Test
  @DisplayName("should handle a process payment event")
  void should_handle_a_process_payment_event() {
    UUID walletId = this.createWalletUseCase.execute("EUR").id();
    UUID paymentId = UUID.randomUUID();
    PaymentCreated event =
        new PaymentCreated(walletId, paymentId.toString(), new BigDecimal("50.00"));

    this.rabbitTemplate.convertAndSend(
        MessagingConfiguration.PLAYTOMIC_EXCHANGE, "payment.new", event);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () ->
                verify(this.processPaymentUseCase, atLeastOnce())
                    .execute(walletId, paymentId.toString(), new BigDecimal("50.00")));
  }

  @Test
  @DisplayName("should handle a payment refunded event")
  void should_handle_a_payment_refunded_event() {
    UUID walletId = UUID.randomUUID();
    String paymentId = UUID.randomUUID().toString();
    PaymentRefunded event =
        new PaymentRefunded(walletId, paymentId, new BigDecimal("75.00"), "USD");

    this.rabbitTemplate.convertAndSend(
        MessagingConfiguration.PLAYTOMIC_EXCHANGE, "payment.refunded", event);

    await()
        .atMost(Duration.ofSeconds(3))
        .pollDelay(Duration.ofMillis(100))
        .until(
            () ->
                this.rabbitAdmin
                        .getQueueInfo(MessagingConfiguration.PAYMENT_EVENTS_QUEUE)
                        .getMessageCount()
                    == 0);
  }
}
