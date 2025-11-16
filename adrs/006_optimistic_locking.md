# ADR-006: Optimistic Locking for Wallet Entities

## Status
Accepted

## Context
During development, the optimistic locking mechanism (using JPA @Version annotation) was identified as necessary for preventing concurrent modification issues in wallet operations, especially for balance updates.

## Decision
Implement optimistic locking for Wallet entity to prevent race conditions in concurrent scenarios:
- Use JPA's `@Version` annotation on `WalletEntity`
- Include version field in domain model for proper mapping
- Retry at most 5 times
- Backoff set to 250ms

## Consequences

### Positive
- **Data Integrity**: Prevents lost updates in concurrent modifications
- **Consistency**: Ensures wallet balance accuracy under concurrent load
- **Standard Practice**: Follows JPA best practices for entity versioning
- **Conflict Detection**: Automatic detection of concurrent modifications

### Negative
- **Complexity**: Requires proper exception handling for version conflicts
- **User Experience**: May require retry mechanisms for failed operations
- **Testing**: Need to test concurrent modification scenarios

## Implementation

### Entity Level
```java
@Entity
@Table(name = "wallet")
public class WalletEntity {

  // other fields...
  
  @Version
  @Column(name = "version", nullable = false)
  private Long version;
}
```

### Domain Model
```java
public class Wallet {

  // other fields...
  
  private final Version version;
  
  public Long version() {
    return this.version.value();
  }
}
```

### Value Object
```java
public record Version(Long value) {

  public Version() {
    this(null);
  }
}
```

## Error Handling Strategy
- Retry when an OptimisticLockingFailureException is thrown
- Provide meaningful error messages to users
- Do not leak details from the stacktrace, map to a user-friendly HTTP response

## Testing Considerations
- Integration tests for concurrent modification scenarios
- Performance tests under concurrent load

## Notes
- Version field starts at 0 for new entities
- JPA automatically increments version on each update
- Version conflicts should trigger appropriate business logic responses
