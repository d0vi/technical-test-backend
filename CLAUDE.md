# Playtomic Wallet Service - Technical Assessment

## Project Overview
This is a wallet service implementation for Playtomic's technical assessment, built using Domain-Driven Design (DDD) principles with Spring Boot 4 and Java 26.

## Architecture

### Domain-Driven Design Structure
```
src/main/java/com/playtomic/tests/wallet/
├── domain/
│   └── model/
│       ├── wallet/
│       │   ├── Wallet.java                    # Aggregate Root
│       │   ├── WalletRepository.java          # Repository Interface
│       │   ├── DomainEventBus.java            # Event Publishing port
│       │   ├── service/PaymentService.java    # Domain Service
│       │   ├── vo/                            # Value Objects (Currency, Balance, WalletId, Audit)
│       │   ├── event/                         # Domain Events (PaymentCreated, WalletCreated, WalletToppedUp, PaymentRefunded)
│       │   └── exception/                     # Domain Exceptions
│       └── transaction/
│           ├── Transaction.java               # Java Record (immutable)
│           ├── TransactionRepository.java
│           ├── Page.java                      # Pagination wrapper record
│           └── vo/                            # TransactionId, TransactionType, PaymentId
├── application/
│   └── usecase/
│       ├── wallet/
│       │   ├── read/GetInfoUseCase.java
│       │   └── write/
│       │       ├── CreateWalletUseCase.java
│       │       ├── TopUpUseCase.java          # Charges payment, publishes PaymentCreated event
│       │       ├── ProcessPaymentUseCase.java # Deposits balance on PaymentCreated event
│       │       └── RefundPaymentUseCase.java  # Refunds payment via provider
│       └── transaction/
│           └── read/GetTransactionsUseCase.java
├── infrastructure/
│   ├── adapter/driven/
│   │   ├── persistence/jpa/              # Database Persistence
│   │   ├── messaging/producer/           # RabbitMQEventPublisher (implements DomainEventBus)
│   │   └── provider/stripe/              # External Payment Provider
│   ├── adapter/driver/
│   │   ├── rest/controller/WalletController.java  # REST API
│   │   └── messaging/consumer/RabbitMQEventListener.java
│   └── configuration/                    # Spring bean wiring (UseCaseConfiguration, MessagingConfiguration, etc.)
└── WalletApplication.java                # Spring Boot Main
```

## Technology Stack

### Core Technologies
- **Java 26**
- **Spring Boot 4**
- **Spring Data JPA** for persistence
- **Spring AMQP** for RabbitMQ messaging
- **H2 Database** for development/testing
- **RabbitMQ** for event-driven architecture

### Testing
- **JUnit 5** for unit testing
- **Mockito** for mocking
- **AssertJ** for fluent assertions
- **Testcontainers** for integration testing
- **Custom Test Data Builders** for test object creation

### Development Tools
- **Lombok** for reducing boilerplate
- **Flyway** for database migrations
- **Spotify's fmt-maven-plugin** for code formatting
- **Maven Surefire/Failsafe** for unit and integration test execution

### CI/CD and Automation
- **GitHub Actions** for continuous integration
- **Dependabot** for automated dependency updates
- **Testcontainers** for integration testing with real services

## Key Features

### Event-Driven Architecture
The system implements an event-driven architecture that separates payment processing from wallet operations:

1. **TopUpUseCase**: Handles payment charging and publishes `PaymentProcessed` events
2. **ProcessPaymentUseCase**: Processes wallet deposits triggered by events
3. **Event Bus**: RabbitMQ-based messaging for decoupled operations

### Domain Events
- `WalletCreated` - Published when a new wallet is created
- `PaymentCreated` - Published when payment is successfully charged (triggers async balance deposit)
- `WalletToppedUp` - Published when wallet balance is updated
- `PaymentRefunded` - Published when a payment is refunded

### Currency Support
- **Multi-currency wallets**: Each wallet is created with a specific currency
- **ISO 4217 compliance**: Supports standard currency codes (EUR, USD, JPY, GBP, CAD)
- **Currency validation**: Strict validation using `Currency` value object and `ISO4217CurrencyCode` enum
- **Domain exception**: `InvalidCurrencyCodeException` for invalid currency codes

### Optimistic Locking
- JPA `@Version` annotation on wallet entities
- Prevents concurrent modification issues
- Ensures data consistency under load

## API Endpoints

### Wallet Operations

The server runs with context path `/playtomic`, so all endpoints are prefixed with `/playtomic`.

```
POST   /api/v1/wallets                        # Create new wallet
GET    /api/v1/wallets/{id}                   # Get wallet information
POST   /api/v1/wallets/{id}/top-up            # Top up wallet balance
GET    /api/v1/wallets/{id}/transactions      # Get paginated transactions (?page=0&size=10)
```

### Request/Response Examples
```json
// Create Wallet
POST /api/v1/wallets
{
  "currency_code": "EUR"
}
Response: {
  "id": "uuid",
  "balance": 0.00,
  "currency": "EUR"
}

// Top Up Wallet
POST /api/v1/wallets/{id}/top-up
{
  "credit_card": "4242424242424242",
  "amount": 100.50
}
Response: {
  "payment_id": "331541d6-617d-4464-b7d0-9b346b87f41c"
}

// Get Wallet Info
GET /api/v1/wallets/{id}
Response: {
  "id": "uuid",
  "balance": 100.50,
  "currency": "EUR"
}
```

## Data Model

### Wallet Entity
- `id` (UUID) - Primary key
- `balance` (BigDecimal) - Current balance
- `currency` (String) - Wallet currency (ISO 4217 code)
- `version` (Long) - Optimistic locking version
- `createdAt` (Timestamp) - Creation timestamp
- `updatedAt` (Timestamp) - Last update timestamp
- `deletedAt` (Timestamp) - Soft deletion timestamp

### Transaction Entity
- `id` (UUID) - Primary key
- `walletId` (UUID) - Wallet id
- `type` (TransactionType) - Type of transaction
- `amount` (BigDecimal) - Amount transfered
- `paymentId` (String) - Payment platform's payment id
- `createdAt` (Timestamp) - Creation timestamp

## Testing Strategy

### Test Structure
- **Unit Tests**: Domain logic and use cases with Mockito
- **Integration Tests**: Full application context with Testcontainers
- **Test Data Builders**: Following Nat Pryce's pattern for object creation

### Test Naming Convention
- `@DisplayName` with descriptive test names
- Method names in `snake_case` format
- AssertJ for fluent assertions over JUnit assertions

### Example Test
```java
@Test
@DisplayName("should create a wallet")
void should_create_a_wallet() {
    Wallet wallet = new Wallet(CURRENCY_EUR);
    
    assertThat(wallet).isNotNull();
    assertThat(wallet.id()).isNotNull();
    assertThat(wallet.balance()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
}
```

## Build and Run

### Prerequisites
- Java 26
- Maven 3.8+
- Docker (for RabbitMQ)

### Build Commands
```bash
# Compile with preview features
mvn clean compile

# Run tests
mvn test

# Run specific test pattern
mvn test -Dtest="*UseCaseTest"

# Start application
mvn spring-boot:run
```


## Infrastructure

### RabbitMQ Setup
```yaml
# compose.yml
services:
  rabbitmq:
    image: rabbitmq:4.1-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      - "RABBITMQ_DEFAULT_USER=admin"
      - "RABBITMQ_DEFAULT_PASS=password"
      - "RABBITMQ_DEFAULT_VHOST=dev"
```

### Database
- H2 in-memory database for development
- Flyway migrations in `src/main/resources/db/migration/`
- JPA entities with proper mapping

## CI/CD Pipeline

### GitHub Actions Workflow
```yaml
# .github/workflows/main.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  merge_group:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: 25
          distribution: temurin
      - run: ./mvnw clean verify
```

### Dependabot Configuration
```yaml
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "maven"
    directory: "/"
    schedule:
      interval: "weekly"
    assignees:
      - "d0vi"
    commit-message:
      prefix: "🤖️"
```

## Architecture Decisions

See `adrs/` directory for detailed Architectural Decision Records:
- [001: Domain-Driven Design Architecture](adrs/001_domain_driven_design.md)
- [002: Hexagonal Architecture Implementation](adrs/002_hexagonal_architecture.md)
- [003: Event-Driven Architecture](adrs/003_event_driven_architecture.md)
- [004: RabbitMQ for Asynchronous Messaging](adrs/004_rabbitmq_messaging.md)
- [005: Comprehensive Testing Strategy](adrs/005_testing_strategy.md)
- [006: Optimistic Locking for Wallet Entities](adrs/006_optimistic_locking.md)

## Development Guidelines

### Code Style
- Use Spotify's fmt-maven-plugin for consistent formatting
- Follow DDD naming conventions
- Prefer composition over inheritance
- Use value objects for domain concepts
- **Favor Java records over classes** for immutable data structures and value objects
- **Use Lombok sparingly** - prefer records or manual implementations

### Domain Design Patterns
- **Value Objects**: Implement as records (e.g., `Currency`, `Balance`, `WalletId`)
- **Domain Events**: Use records for immutable event data
- **DTOs**: Implement REST request/response objects as records
- **Entities**: Use classes with proper encapsulation (e.g., `Wallet`, `Transaction`)

### Testing Practices
- Use Test Data Builders for object creation
- Test business logic, not implementation details
- Use descriptive test names with `@DisplayName`
- **Method names in snake_case format** for test methods
- Prefer AssertJ over JUnit assertions
- **Follow current testing structure**: Unit tests with `*Test.java`, Integration tests with `*IT.java`

### Event Handling
- Publish domain events for significant business operations
- Design for eventual consistency
- Implement proper error handling and retry mechanisms
- Use meaningful event names and payload structures

### Git Commit Conventions
- **Start with an emoji** that represents the type of change
- **Use imperative mood** (e.g., "Add feature" not "Added feature")
- **Keep messages concise** and descriptive
- **Focus on what was done**, not how it was done

## Future Considerations

### Scalability
- Consider CQRS for read/write separation
- Implement proper event sourcing if needed
- Database partitioning for large datasets

### Monitoring
- Add distributed tracing (OpenTelemetry)
- Implement business metrics
- Add health checks and monitoring endpoints

### Security
- Implement proper authentication/authorization
- Add input validation and sanitization
- Secure sensitive data (credit card information)
