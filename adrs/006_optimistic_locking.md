# ADR-006: Optimistic Locking for Wallet Entities

## Status
Accepted

## Context
During development, the optimistic locking mechanism (using JPA @Version annotation) was temporarily removed but later identified as necessary for preventing concurrent modification issues in wallet operations, especially for balance updates.

## Decision
Restore and maintain optimistic locking for Wallet entities to prevent race conditions in concurrent scenarios:
- Use JPA @Version annotation on WalletEntity
- Include version field in domain model for proper mapping
- Handle OptimisticLockException appropriately in use cases

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
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    
    // other fields...
}
```

### Domain Model
```java
public class Wallet {
    private final Version version;
    
    public Long version() {
        return this.version.value();
    }
}
```

### Value Object
```java
public class Version {
    private final Long value;
    
    public Version() {
        this.value = 0L;
    }
    
    public Version(Long value) {
        this.value = value != null ? value : 0L;
    }
    
    public Long value() {
        return value;
    }
}
```

## Error Handling Strategy
- Catch OptimisticLockException in repository layer
- Translate to domain-specific exceptions
- Implement retry logic where appropriate
- Provide meaningful error messages to users

## Testing Considerations
- Unit tests for version handling in domain objects
- Integration tests for concurrent modification scenarios
- Performance tests under concurrent load

## Notes
- Version field starts at 0 for new entities
- JPA automatically increments version on each update
- Version conflicts should trigger appropriate business logic responses