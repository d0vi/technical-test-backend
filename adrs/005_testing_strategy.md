# ADR-005: Comprehensive Testing Strategy

## Status
Accepted

## Context
Financial applications require rigorous testing to ensure correctness, reliability, and maintainability. The wallet service needs a comprehensive testing strategy that covers unit tests, integration tests, and end-to-end scenarios while maintaining fast feedback loops and high confidence in deployments.

## Decision
Implement a multi-layered testing strategy that follows the test pyramid principles:

- **Unit Tests**: Fast, isolated tests for business logic using Test Data Builders
- **Integration Tests**: Component integration with real infrastructure using Testcontainers
- **Contract Tests**: Event schema validation and API contract verification
- **Architecture Tests**: Ensure architectural boundaries and dependency rules

## Consequences

### Positive
- **Fast Feedback**: Unit tests provide immediate feedback during development
- **High Confidence**: Integration tests catch configuration and integration issues
- **Maintainable Tests**: Test Data Builders make tests readable and maintainable
- **Architecture Enforcement**: Architecture tests prevent dependency violations
- **Event Reliability**: Contract tests ensure event compatibility across services

### Negative
- **Test Complexity**: Multiple test types require different skills and tools
- **Maintenance Overhead**: More tests mean more code to maintain
- **Build Time**: Integration tests with containers can slow down CI/CD

## Implementation

### Test Structure
```
src/test/java/
├── unit/
│   ├── domain/                     # Domain logic tests
│   ├── application/usecase/        # Use case tests with mocks
│   └── helper/WalletTestBuilder.java # Test Data Builders
├── integration/
│   ├── persistence/               # Database integration tests
│   ├── messaging/                 # RabbitMQ integration tests
│   └── api/                       # REST API integration tests
└── architecture/
    └── ArchitectureTest.java      # Dependency rule verification
```

### Unit Testing Strategy
```java
// Domain logic testing
@ExtendWith(MockitoExtension.class)
class CreateWalletUseCaseTest {
  @Mock private WalletRepository walletRepository;
  @Mock private DomainEventBus domainEventBus;
  @InjectMocks private CreateWalletUseCase createWalletUseCase;

  @Test
  @DisplayName("should create a wallet")
  void should_create_a_wallet() {
    Wallet expectedWallet = new WalletTestBuilder().build();
    when(walletRepository.save(any(Wallet.class))).thenReturn(expectedWallet);

    Wallet result = createWalletUseCase.execute();

    assertThat(result).isEqualTo(expectedWallet);
    verify(domainEventBus).publishDomainEvent(any(WalletCreated.class));
  }
}
```

### Test Data Builders
```java
public class WalletTestBuilder {
  String id = UUID.randomUUID().toString();
  BigDecimal balance = BigDecimal.ZERO;
  Long version = 0L;
  LocalDateTime createdAt = LocalDateTime.now();
  List<Transaction> transactions = new ArrayList<>();

  public WalletTestBuilder withBalance(String balance) {
    this.balance = new BigDecimal(balance);
    return this;
  }

  public Wallet build() {
    return new Wallet(id, balance, version, createdAt, updatedAt, deletedAt, transactions);
  }
}
```

### Integration Testing with Testcontainers
```java
@SpringBootTest
@Testcontainers
class WalletIntegrationTest {
  @Container
  static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

  @Test
  @DisplayName("should publish event when wallet is topped up")
  void should_publish_event_when_wallet_is_topped_up() {
    // Given: wallet exists
    Wallet wallet = createWallet();
    
    // When: top up wallet
    topUpWallet(wallet.id(), "4242424242424242", new BigDecimal("100.00"));
    
    // Then: verify event published
    verifyEventPublished(PaymentProcessed.class);
  }
}
```

### Contract Testing
```java
@Test
@DisplayName("should maintain event schema compatibility")
void should_maintain_event_schema_compatibility() {
  PaymentProcessed event = new PaymentProcessed(
      UUID.randomUUID(),
      "payment_123",
      new BigDecimal("100.00"),
      "credit_card",
      LocalDateTime.now()
  );
  
  String json = objectMapper.writeValueAsString(event);
  PaymentProcessed deserialized = objectMapper.readValue(json, PaymentProcessed.class);
  
  assertThat(deserialized).isEqualTo(event);
}
```

### Architecture Testing
```java
@AnalyzeClasses(packages = "com.playtomic.tests.wallet")
class ArchitectureTest {
  
  @Test
  void domain_should_not_depend_on_infrastructure() {
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAPackage("..infrastructure..")
        .check(importedClasses);
  }
  
  @Test
  void use_cases_should_not_depend_on_controllers() {
    noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat()
        .resideInAPackage("..adapter.driving..")
        .check(importedClasses);
  }
}
```

## Testing Guidelines

### Test Naming Convention
- Use `@DisplayName` with clear, business-focused descriptions
- Method names in `snake_case` format for readability
- Focus on behavior rather than implementation details

### Assertion Strategy
- Prefer AssertJ fluent assertions over JUnit assertions
- Test business behavior, not internal implementation
- Use meaningful assertion messages for failures

### Mock Strategy
- Mock external dependencies (databases, messaging, external APIs)
- Don't mock value objects or domain entities
- Verify important interactions but avoid over-verification

## Test Execution Strategy
```bash
# Fast unit tests for development feedback
mvn test -Dtest="*Test"

# Integration tests for CI/CD
mvn test -Dtest="*IntegrationTest" 

# All tests
mvn verify
```

## Quality Gates
- **Code Coverage**: Minimum 80% line coverage for domain and application layers
- **Test Naming**: All tests must have meaningful `@DisplayName` annotations
- **Architecture Rules**: All architecture tests must pass
- **Performance**: Unit test suite must complete in under 30 seconds

## Benefits Realized
- **Confidence**: High test coverage provides confidence in refactoring
- **Documentation**: Tests serve as living documentation of business behavior
- **Regression Prevention**: Comprehensive test suite catches regressions early
- **Architecture Compliance**: Architecture tests maintain clean boundaries

## Notes
- Test Data Builders eliminate repetitive test setup code
- Testcontainers provide realistic integration testing without external dependencies
- Architecture tests prevent architectural drift over time
- Event contract tests ensure backward compatibility during system evolution