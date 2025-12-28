package com.playtomic.tests.wallet.infrastructure.configuration;

import com.playtomic.tests.wallet.domain.model.wallet.service.PaymentService;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.provider.stripe.StripeRestTemplateResponseErrorHandler;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.provider.stripe.StripeService;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;

@Configuration
public class ExternalServiceConfiguration {

  @Bean
  public ResponseErrorHandler stripeRestTemplateResponseErrorHandler() {
    return new StripeRestTemplateResponseErrorHandler();
  }

  @Bean
  public PaymentService paymentService(
      @Value("${stripe.simulator.charges-uri}") final URI chargesUri,
      @Value("${stripe.simulator.refunds-uri}") final URI refundsUri,
      final RestTemplateBuilder restTemplateBuilder) {
    return new StripeService(
        chargesUri,
        refundsUri,
        restTemplateBuilder.errorHandler(new StripeRestTemplateResponseErrorHandler()).build());
  }
}
