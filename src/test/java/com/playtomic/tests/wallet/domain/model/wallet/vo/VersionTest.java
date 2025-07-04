package com.playtomic.tests.wallet.domain.model.wallet.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VersionTest {

  @Test
  @DisplayName("should create a version")
  void should_create_a_version() {
    Version version = new Version();

    assertThat(version.value()).isNull();
  }

  @Test
  @DisplayName("should create a version with value")
  void should_create_a_version_with_value() {
    Long value = 5L;

    Version version = new Version(value);

    assertThat(version.value()).isEqualTo(value);
  }
}
