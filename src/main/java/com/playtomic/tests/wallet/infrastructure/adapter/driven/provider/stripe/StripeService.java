package com.playtomic.tests.wallet.infrastructure.adapter.driven.provider.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playtomic.tests.wallet.domain.model.wallet.service.PaymentService;
import java.math.BigDecimal;
import java.net.URI;
import lombok.NonNull;
import org.springframework.web.client.RestTemplate;

/// Handles the communication with Stripe.
///
/// A real implementation would call to Stripe using their API/SDK. This dummy implementation
/// throws an error when trying to charge less than 10€.
public class StripeService implements PaymentService {

  private final URI chargesUri;
  private final URI refundsUri;
  private final RestTemplate restTemplate;

  public StripeService(URI chargesUri, URI refundsUri, RestTemplate restTemplate) {
    this.chargesUri = chargesUri;
    this.refundsUri = refundsUri;
    this.restTemplate = restTemplate;
  }

  /// Charges money in the credit card.
  ///
  /// Ignore the fact that no CVC or expiration date are provided.
  ///
  /// @param creditCardNumber The number of the credit card
  /// @param amount The amount that will be charged.
  /// @throws StripeServiceException if the amount is too low
  @Override
  public String charge(@NonNull String creditCardNumber, @NonNull BigDecimal amount)
      throws StripeServiceException {
    Payment payment =
        this.restTemplate.postForObject(
            this.chargesUri, new ChargeRequest(creditCardNumber, amount), Payment.class);
    return payment != null ? payment.id() : null;
  }

  /// Refunds the specified payment.
  @Override
  public void refund(@NonNull String paymentId) throws StripeServiceException {
    // Object.class because we don't read the body here.
    this.restTemplate.postForEntity(this.refundsUri.toString(), null, Object.class, paymentId);
  }

  private record ChargeRequest(
      @JsonProperty("credit_card") String creditCardNumber,
      @JsonProperty("amount") BigDecimal amount) {}
}
