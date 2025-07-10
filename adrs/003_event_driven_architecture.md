# ADR-003: Event-Driven Architecture for Payment Processing

## Status
Accepted

## Context
Initial implementation had the TopUpUseCase directly processing both payment charging and wallet deposit operations in a single transaction. This created tight coupling between payment processing and wallet operations, making the system less resilient and harder to scale.

## Decision
Implement an event-driven architecture that separates payment processing from wallet operations:

1. **TopUpUseCase**: Only handles payment charging and publishes PaymentProcessed event
2. **ProcessPaymentUseCase**: Handles wallet deposits triggered by PaymentProcessed events
3. **Event Bus**: Decouples the two operations through asynchronous messaging

## Consequences

### Positive
- **Separation of Concerns**: Payment processing and wallet operations are decoupled
- **Resilience**: Failed wallet operations don't affect payment processing
- **Scalability**: Can scale payment and wallet services independently
- **Retry Capabilities**: Failed wallet operations can be retried through message queues
- **Auditability**: Clear event trail for all operations

### Negative
- **Eventual Consistency**: Wallet balance updates are not immediate
- **Complexity**: More complex flow with multiple components
- **Error Handling**: Need sophisticated error handling and compensation mechanisms

## Implementation

### Events
```java
public record PaymentProcessed(
    UUID walletId,
    String paymentId,
    BigDecimal amount,
    String paymentMethod,
    LocalDateTime timestamp
) {}

public record WalletToppedUp(
    UUID walletId,
    String paymentId,
    BigDecimal amount,
    BigDecimal previousBalance,
    BigDecimal newBalance,
    LocalDateTime timestamp
) {}
```

### Use Cases
- **TopUpUseCase**: Validates wallet → Charges payment → Publishes PaymentProcessed event
- **ProcessPaymentUseCase**: Receives PaymentProcessed → Updates wallet balance → Publishes WalletToppedUp event

### Event Listeners
```java
@RabbitListener(queues = "payment.processed")
public void handlePaymentProcessed(PaymentProcessed event) {
    processPaymentUseCase.execute(event.walletId(), event.paymentId(), event.amount());
}
```

## Retry Policy
- Configured dead letter queues for failed message processing
- Exponential backoff retry strategy
- Maximum retry attempts with final dead letter handling

## Notes
- Enables future horizontal scaling of payment and wallet services
- Provides foundation for other event-driven features (notifications, analytics)
- Maintains data consistency through eventual consistency model