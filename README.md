[![Build status](https://github.com/d0vi/technical-test-backend/workflows/CI/badge.svg)](https://github.com/d0vi/technical-test-backend/actions/workflows/main.yml)

# 💰 Wallet Service

Managing your digital wallet never felt so easy 🫢️

### 🚀 Features

- **💳 Wallet Management**: Query balances and manage digital wallets
- **🔄 Top-up Operations**: Add funds using credit cards via external payment providers
- **💼 Transaction History**: Track all wallet operations and payments
- **🔐 Secure Payments**: Integration with Stripe for secure payment processing
- **📊 Event-Driven**: RabbitMQ integration for real-time event processing

### 🛠️ Tech Stack

- **Spring Boot 4** - Modern Java framework
- **Spring Data JPA** - Database operations
- **H2 Database** - In-memory database for development
- **Flyway** - Database migrations
- **RabbitMQ** - Message broker for events
- **WireMock** - HTTP service mocking for tests
- **Maven** - Dependency management

### ➡️ Requirements

In order to run this application, a valid JDK 26 version must be present in your system.

Use [SDKMAN!](https://sdkman.io/) to install the Eclipse Temurin JDK:
```
sdk install java 26-tem
```

### 🏃🏻‍♂️ Run the application

Open a new terminal and execute:

```
docker compose up -d

./mvnw spring-boot:run
```

Or, if you wish to run the complete test suite:

```
./mvnw verify
```

### 📡 API Endpoints

- `POST /playtomic/api/v1/wallets` - Create a new wallet
- `GET /playtomic/api/v1/wallets/{id}` - Get wallet information
- `POST /playtomic/api/v1/wallets/{id}/top-up` - Add funds to wallet

### 🏗️ Architecture

Built with **Hexagonal Architecture** principles:
- **Domain Layer**: Core business logic
- **Application Layer**: Use cases and services  
- **Infrastructure Layer**: External integrations (DB, payments, messaging)

### 🧪 Testing

Comprehensive test coverage including:
- Unit tests for business logic
- Integration tests for external services
- ⚠️ TODO: End-to-end API tests
