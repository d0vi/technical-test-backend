package com.playtomic.tests.wallet;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;

@SpringBootTest(classes = {WalletApplication.class})
@ActiveProfiles(profiles = "test")
public class WalletApplicationIT {

  static RabbitMQContainer rabbitMQ =
      new RabbitMQContainer("rabbitmq:4.2").withEnv("RABBITMQ_DEFAULT_VHOST", "test");

  static {
    rabbitMQ.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
    registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
  }
}
