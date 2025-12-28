package com.playtomic.tests.wallet;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@Testcontainers
public class WalletApplicationIT {

  @Container
  static RabbitMQContainer rabbitMQ =
      new RabbitMQContainer("rabbitmq:4.2").withEnv("RABBITMQ_DEFAULT_VHOST", "test");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
    registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
  }
}
