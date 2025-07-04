package com.playtomic.tests.wallet.infrastructure.adapter.driven.provider.stripe;

import java.math.BigDecimal;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class StripeServiceTest {

  URI testUri = URI.create("http://how-would-you-test-me.localhost");
  StripeService s = new StripeService(testUri, testUri, new RestTemplate());

  @Test
  public void test_exception() {
    Assertions.assertThrows(
        StripeAmountTooSmallException.class,
        () -> {
          s.charge("4242 4242 4242 4242", new BigDecimal(5));
        });
  }

  @Test
  public void test_ok() throws StripeServiceException {
    s.charge("4242 4242 4242 4242", new BigDecimal(15));
  }
}
