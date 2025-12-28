package com.playtomic.tests.wallet.infrastructure.adapter.driven.provider.stripe;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.playtomic.tests.wallet.WalletApplicationIT;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

@EnableWireMock({@ConfigureWireMock(port = 9999)})
public class StripeServiceIT extends WalletApplicationIT {

  @Autowired private StripeService stripeService;

  @Test
  public void test_exception() {
    stubFor(post(urlEqualTo("/charges")).willReturn(aResponse().withStatus(422)));

    assertThatThrownBy(() -> this.stripeService.charge("4242 4242 4242 4242", new BigDecimal(5)))
        .isInstanceOf(StripeAmountTooSmallException.class);
  }

  @Test
  public void test_ok() throws StripeServiceException {
    stubFor(
        post(urlEqualTo("/charges"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                {
                  "id": "bd4e65ad-9f43-4939-b176-0f428f46bea6",
                  "amount": 15
                }
                """)));

    String paymentId = this.stripeService.charge("4242 4242 4242 4242", new BigDecimal(15));

    verify(
        postRequestedFor(urlEqualTo("/charges"))
            .withRequestBody(
                equalToJson(
                    """
            {
              "credit_card": "4242 4242 4242 4242",
              "amount": 15
            }
            """)));
    assertThat(paymentId).isEqualTo("bd4e65ad-9f43-4939-b176-0f428f46bea6");
  }
}
