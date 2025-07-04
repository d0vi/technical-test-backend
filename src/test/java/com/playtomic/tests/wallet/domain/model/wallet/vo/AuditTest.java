package com.playtomic.tests.wallet.domain.model.wallet.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidAuditStateException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuditTest {

  @Test
  @DisplayName("should create an audit")
  void should_create_an_audit() {
    LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
    LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);
    LocalDateTime deletedAt = LocalDateTime.now();

    Audit audit = new Audit(createdAt, updatedAt, deletedAt);

    assertThat(audit.createdAt()).isEqualTo(createdAt);
    assertThat(audit.updatedAt()).isEqualTo(updatedAt);
    assertThat(audit.deletedAt()).isEqualTo(deletedAt);
  }

  @Test
  @DisplayName("should create an audit with convenience constructor")
  void should_create_an_audit_with_convenience_constructor() {
    LocalDateTime beforeCreation = LocalDateTime.now();

    Audit audit = new Audit();

    LocalDateTime afterCreation = LocalDateTime.now();

    assertThat(audit.createdAt()).isBetween(beforeCreation, afterCreation);
    assertThat(audit.updatedAt()).isNull();
    assertThat(audit.deletedAt()).isNull();
  }

  @Test
  @DisplayName("should throw an exception when createdAt is null")
  void should_throw_an_exception_when_created_at_is_null() {
    assertThatThrownBy(() -> new Audit(null, LocalDateTime.now(), LocalDateTime.now()))
        .isInstanceOf(InvalidAuditStateException.class)
        .hasMessage("Wallet creation date must not be null");
  }

  @Test
  @DisplayName("should throw an exception when createdAt is after updatedAt")
  void should_throw_an_exception_when_created_at_is_after_updated_at() {
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);

    assertThatThrownBy(() -> new Audit(createdAt, updatedAt, null))
        .isInstanceOf(InvalidAuditStateException.class)
        .hasMessage("Wallet creation date must be before modification date");
  }

  @Test
  @DisplayName("should throw an exception when updatedAt is after deletedAt")
  void should_throw_an_exception_when_updated_at_is_after_deleted_at() {
    LocalDateTime createdAt = LocalDateTime.now().minusHours(3);
    LocalDateTime updatedAt = LocalDateTime.now();
    LocalDateTime deletedAt = LocalDateTime.now().minusHours(1);

    assertThatThrownBy(() -> new Audit(createdAt, updatedAt, deletedAt))
        .isInstanceOf(InvalidAuditStateException.class)
        .hasMessage("Wallet modification date must be before deletion date");
  }
}
