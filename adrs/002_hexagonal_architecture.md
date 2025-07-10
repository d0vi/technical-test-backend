# ADR-002: Hexagonal Architecture Implementation

## Status
Accepted

## Context
The wallet service needs to maintain clean separation between business logic and external concerns (databases, web frameworks, messaging systems). Traditional layered architectures often lead to tight coupling between business logic and infrastructure concerns, making the system difficult to test and evolve.

## Decision
Implement Hexagonal Architecture (Ports and Adapters) to create clear boundaries between the domain core and external systems:

- **Domain Core**: Contains business logic, independent of external frameworks
- **Ports**: Interfaces that define contracts for external interactions
- **Adapters**: Implementations that connect ports to specific technologies
- **Driving Adapters**: External systems that initiate operations (REST controllers)
- **Driven Adapters**: External systems that the application uses (databases, messaging)

## Consequences

### Positive
- **Technology Independence**: Business logic is decoupled from specific frameworks
- **Testability**: Easy to test business logic with mock adapters
- **Flexibility**: Can swap implementations without changing business logic
- **Maintainability**: Clear boundaries prevent infrastructure concerns from leaking into domain
- **Evolution**: Easy to add new interfaces (GraphQL, gRPC) or change databases

### Negative
- **Initial Complexity**: More interfaces and abstractions to design
- **Learning Curve**: Team needs to understand hexagonal concepts
- **Potential Over-Abstraction**: May introduce unnecessary complexity for simple operations

## Implementation

### Architecture Structure
```
src/main/java/com/playtomic/tests/wallet/
├── domain/                          # Inner hexagon (business logic)
│   └── model/wallet/
│       ├── Wallet.java              # Domain entities
│       ├── WalletRepository.java    # Port (interface)
│       ├── DomainEventBus.java      # Port (interface)
│       └── service/PaymentService.java # Port (interface)
├── application/                     # Application services (use cases)
│   └── usecase/wallet/
│       ├── CreateWalletUseCase.java
│       ├── TopUpUseCase.java
│       └── ProcessPaymentUseCase.java
└── infrastructure/                  # Outer hexagon (adapters)
    ├── adapter/
    │   ├── driving/                 # Primary adapters (input)
    │   │   └── rest/WalletController.java
    │   └── driven/                  # Secondary adapters (output)
    │       ├── persistence/jpa/     # Database adapter
    │       ├── messaging/rabbitmq/  # Event bus adapter
    │       └── provider/stripe/     # Payment service adapter
```

### Port Examples
```java
// Domain port (interface)
public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(UUID id);
}

// Domain port (interface)
public interface DomainEventBus {
    void publishDomainEvent(DomainEvent event);
}
```

### Adapter Examples
```java
// Driving adapter (REST)
@RestController
public class WalletController {
    private final CreateWalletUseCase createWalletUseCase;
    
    @PostMapping("/wallets")
    public ResponseEntity<WalletResponse> createWallet() {
        Wallet wallet = createWalletUseCase.execute();
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }
}

// Driven adapter (JPA)
@Component
public class JpaWalletRepository implements WalletRepository {
    private final WalletJpaRepository jpaRepository;
    private final WalletEntityMapper mapper;
    
    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = mapper.toEntity(wallet);
        WalletEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

### Dependency Direction
```
Infrastructure → Application → Domain
     ↓              ↓            ↑
  Adapters    ←  Use Cases  ←  Ports
```

## Benefits Realized
- **Independent Testing**: Domain logic can be tested without Spring context
- **Technology Flexibility**: Easy to switch from H2 to PostgreSQL, or REST to GraphQL
- **Clean Dependencies**: Domain layer has no external dependencies
- **Clear Responsibilities**: Each layer has a single, well-defined purpose

## Testing Strategy
- **Unit Tests**: Test domain logic with mock ports
- **Integration Tests**: Test adapters with real external systems
- **Architecture Tests**: Verify dependency rules with ArchUnit

## Notes
- Hexagonal architecture particularly valuable for financial systems requiring high reliability
- Port interfaces belong to the domain layer, not infrastructure
- Use cases orchestrate domain objects and ports to fulfill business operations
- Adapters translate between domain language and external system protocols