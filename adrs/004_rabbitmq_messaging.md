# ADR-004: RabbitMQ for Asynchronous Messaging

## Status
Accepted

## Context
The wallet service requires asynchronous messaging for event-driven architecture to decouple payment processing from wallet operations. The system needs reliable message delivery, retry mechanisms, and dead letter handling for failed message processing.

## Decision
Adopt RabbitMQ as the message broker for asynchronous communication between bounded contexts:

- **Event Publishing**: Domain events published to RabbitMQ exchanges
- **Event Consumption**: Use case handlers consume events from dedicated queues
- **Retry Logic**: Configure retry policies with exponential backoff
- **Dead Letter Queues**: Handle permanently failed messages
- **Spring AMQP Integration**: Leverage Spring Boot's RabbitMQ support

## Consequences

### Positive
- **Decoupling**: Payment and wallet operations are independently scalable
- **Reliability**: Built-in message persistence and acknowledgment mechanisms
- **Resilience**: Retry policies and dead letter queues handle transient failures
- **Observability**: Management UI provides visibility into message flows
- **Development Speed**: Spring AMQP simplifies configuration and usage

### Negative
- **Eventual Consistency**: Operations are not immediately consistent
- **Complexity**: Additional infrastructure component to manage
- **Debugging**: Distributed message flows can be harder to trace
- **Operational Overhead**: Requires monitoring and maintenance of message queues

## Implementation

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### Configuration
```java
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public TopicExchange walletExchange() {
        return new TopicExchange("wallet.exchange");
    }
    
    @Bean
    public Queue paymentProcessedQueue() {
        return QueueBuilder.durable("payment.processed")
            .withArgument("x-dead-letter-exchange", "wallet.dlx")
            .build();
    }
    
    @Bean
    public Binding paymentProcessedBinding() {
        return BindingBuilder.bind(paymentProcessedQueue())
            .to(walletExchange())
            .with("payment.processed");
    }
}
```

### Event Publishing
```java
@Component
public class RabbitMQEventBus implements DomainEventBus {
    private final RabbitTemplate rabbitTemplate;
    
    @Override
    public void publishDomainEvent(DomainEvent event) {
        String routingKey = getRoutingKey(event);
        rabbitTemplate.convertAndSend("wallet.exchange", routingKey, event);
    }
}
```

### Event Consumption
```java
@Component
public class PaymentEventListener {
    
    @RabbitListener(queues = "payment.processed")
    public void handlePaymentProcessed(PaymentProcessed event) {
        processPaymentUseCase.execute(event.walletId(), event.paymentId(), event.amount());
    }
}
```

### Infrastructure Setup
```yaml
# docker-compose.yml
services:
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"      # AMQP port
      - "15672:15672"    # Management UI
    environment:
      RABBITMQ_DEFAULT_USER: user
      RABBITMQ_DEFAULT_PASS: password
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
```

### Application Properties
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: user
    password: password
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 1s
          max-attempts: 3
          multiplier: 2
```

## Event Flow
1. **TopUpUseCase** charges payment and publishes `PaymentProcessed` event
2. **RabbitMQ** routes event to `payment.processed` queue
3. **PaymentEventListener** consumes event and triggers `ProcessPaymentUseCase`
4. **ProcessPaymentUseCase** updates wallet balance and publishes `WalletToppedUp` event

## Monitoring and Observability
- **Management UI**: Available at http://localhost:15672 for queue monitoring
- **Dead Letter Queues**: Failed messages routed to DLX for investigation
- **Metrics**: Queue depth, message rates, and consumer performance
- **Logging**: Structured logging for event publishing and consumption

## Testing Strategy
- **Unit Tests**: Mock RabbitTemplate for event publishing tests
- **Integration Tests**: Use Testcontainers with real RabbitMQ instance
- **Message Contracts**: Verify event schemas and routing keys

## Notes
- RabbitMQ provides excellent balance of features and operational simplicity
- Topic exchanges enable flexible routing patterns for future bounded contexts
- Dead letter queues are essential for financial systems to prevent data loss
- Consider message versioning strategy for future event schema evolution
