package com.playtomic.tests.wallet.infrastructure.configuration;

import com.playtomic.tests.wallet.application.usecase.wallet.write.ProcessPaymentUseCase;
import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.messaging.producer.RabbitMQEventPublisher;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.messaging.consumer.RabbitMQEventListener;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {

  public static final String PLAYTOMIC_EXCHANGE = "playtomic.exchange";

  public static final String PAYMENT_EVENTS_QUEUE = "playtomic.payment.events";
  public static final String PAYMENT_EVENTS_RETRY_QUEUE = "playtomic.payment.events.retry";
  public static final String PAYMENT_EVENTS_DLQ = "playtomic.payment.events.dlq";

  public static final String WALLET_EVENTS_QUEUE = "playtomic.wallet.events";
  public static final String WALLET_EVENTS_RETRY_QUEUE = "playtomic.wallet.events.retry";
  public static final String WALLET_EVENTS_DLQ = "playtomic.wallet.events.dlq";

  public static final String PAYMENT_ROUTING_KEY = "payment.*";
  public static final String WALLET_ROUTING_KEY = "wallet.*";
  private static final Long BACKOFF = 1000L;

  @Bean
  public TopicExchange playtomicExchange() {
    return new TopicExchange(PLAYTOMIC_EXCHANGE);
  }

  @Bean
  public Queue paymentEventsQueue() {
    return QueueBuilder.durable(PAYMENT_EVENTS_QUEUE)
        .withArgument("x-dead-letter-exchange", PLAYTOMIC_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", "payment.retry")
        .build();
  }

  @Bean
  public Queue paymentEventsRetryQueue() {
    return QueueBuilder.durable(PAYMENT_EVENTS_RETRY_QUEUE)
        .withArgument("x-dead-letter-exchange", PLAYTOMIC_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", "payment.new")
        .withArgument("x-message-ttl", BACKOFF)
        .build();
  }

  @Bean
  public Queue paymentEventsDlq() {
    return new Queue(PAYMENT_EVENTS_DLQ, true);
  }

  @Bean
  public Queue walletEventsQueue() {
    return QueueBuilder.durable(WALLET_EVENTS_QUEUE)
        .withArgument("x-dead-letter-exchange", PLAYTOMIC_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", "wallet.retry")
        .build();
  }

  @Bean
  public Queue walletEventsRetryQueue() {
    return QueueBuilder.durable(WALLET_EVENTS_RETRY_QUEUE)
        .withArgument("x-dead-letter-exchange", PLAYTOMIC_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", "wallet.new")
        .withArgument("x-message-ttl", BACKOFF)
        .build();
  }

  @Bean
  public Queue walletEventsDlq() {
    return new Queue(WALLET_EVENTS_DLQ, true);
  }

  @Bean
  public Binding paymentEventsBinding() {
    return BindingBuilder.bind(paymentEventsQueue())
        .to(playtomicExchange())
        .with(PAYMENT_ROUTING_KEY);
  }

  @Bean
  public Binding paymentEventsRetryBinding() {
    return BindingBuilder.bind(paymentEventsRetryQueue())
        .to(playtomicExchange())
        .with("payment.retry");
  }

  @Bean
  public Binding paymentEventsDlqBinding() {
    return BindingBuilder.bind(paymentEventsDlq()).to(playtomicExchange()).with("payment.bad");
  }

  @Bean
  public Binding walletEventsBinding() {
    return BindingBuilder.bind(walletEventsQueue())
        .to(playtomicExchange())
        .with(WALLET_ROUTING_KEY);
  }

  @Bean
  public Binding walletEventsRetryBinding() {
    return BindingBuilder.bind(walletEventsRetryQueue())
        .to(playtomicExchange())
        .with("wallet.retry");
  }

  @Bean
  public Binding walletEventsDlqBinding() {
    return BindingBuilder.bind(walletEventsDlq()).to(playtomicExchange()).with("wallet.bad");
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter());
    return template;
  }

  @Bean
  public DomainEventBus domainEventBus(final RabbitTemplate rabbitTemplate) {
    return new RabbitMQEventPublisher(rabbitTemplate);
  }

  @Bean
  public RabbitMQEventListener eventListener(final ProcessPaymentUseCase processPaymentUseCase) {
    return new RabbitMQEventListener(processPaymentUseCase);
  }
}
