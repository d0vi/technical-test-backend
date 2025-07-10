# ADR-001: Domain-Driven Design Architecture

## Status
Accepted

## Context
The wallet service requires a clear, maintainable architecture that can handle complex business logic around financial transactions. Traditional layered architectures often lead to anemic domain models and business logic scattered across service layers.

## Decision
Implement Domain-Driven Design (DDD) principles to create a rich domain model that encapsulates business logic and maintains clear boundaries between different concerns:

- **Aggregate Roots**: Wallet as the main aggregate root controlling access to transactions
- **Value Objects**: Immutable objects for concepts like WalletId, Balance, Version, and Audit
- **Domain Services**: PaymentService for operations that don't naturally belong to entities
- **Repository Pattern**: Abstract data access through domain interfaces
- **Domain Events**: Capture and communicate important business events

## Consequences

### Positive
- **Business Logic Centralization**: Domain logic lives in domain objects, not service layers
- **Ubiquitous Language**: Code reflects business terminology and concepts
- **Testability**: Rich domain models are easier to unit test in isolation
- **Maintainability**: Clear separation of concerns and well-defined boundaries
- **Consistency**: Aggregate roots enforce business invariants and consistency rules

### Negative
- **Learning Curve**: Team needs to understand DDD concepts and patterns
- **Initial Complexity**: More upfront design effort compared to simple CRUD approaches
- **Potential Over-Engineering**: Risk of applying DDD to simple domains that don't warrant it

## Implementation

### Domain Structure
```
domain/
├── model/wallet/
│   ├── Wallet.java              # Aggregate Root
│   ├── Transaction.java         # Entity (Java Record)
│   ├── WalletRepository.java    # Repository Interface
│   ├── DomainEventBus.java      # Event Publishing
│   ├── service/
│   │   └── PaymentService.java  # Domain Service
│   ├── vo/                      # Value Objects
│   │   ├── WalletId.java
│   │   ├── Balance.java
│   │   ├── Version.java
│   │   └── Audit.java
│   ├── event/                   # Domain Events
│   │   ├── WalletCreated.java
│   │   ├── PaymentProcessed.java
│   │   └── WalletToppedUp.java
│   └── exception/               # Domain Exceptions
```

### Aggregate Root Example
```java
public class Wallet {
  private final WalletId id;
  private Balance balance;
  private final Version version;
  private Audit audit;
  private final List<Transaction> transactions;

  public void deposit(BigDecimal amount, String paymentId) {
    if (amount == null || amount.doubleValue() <= 0) {
      throw new InvalidAmountException();
    }
    this.balance = this.balance.add(amount);
    this.audit = new Audit(this.audit.createdAt(), LocalDateTime.now(), null);
    this.transactions.add(new Transaction(this.id(), amount, paymentId));
  }
}
```

### Value Objects
```java
public class Balance {
  private final BigDecimal amount;
  
  public Balance(BigDecimal amount) {
    this.amount = amount != null ? amount : BigDecimal.ZERO;
  }
  
  public Balance add(BigDecimal value) {
    return new Balance(this.amount.add(value));
  }
}
```

## Benefits Realized
- Business rules are clearly expressed in domain objects
- Wallet aggregate ensures consistency of balance and transaction operations
- Value objects provide type safety and encapsulate validation logic
- Domain events enable loose coupling between bounded contexts

## Notes
- DDD is particularly valuable for financial domains with complex business rules
- Aggregate boundaries prevent inconsistent state changes
- Domain events provide audit trail and enable event-driven architecture