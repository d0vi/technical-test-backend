package com.playtomic.tests.wallet.e2e;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.WalletApplicationIT;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request.CreateWalletRequest;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request.TopUpRequest;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response.TopUpResponse;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response.WalletSummaryResponse;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 9999)
class WalletE2ETest extends WalletApplicationIT {

  private static final String CURRENCY_EUR = "EUR";
  private static final String CURRENCY_USD = "USD";
  private static final String CREDIT_CARD = "4242424242424242";
  private static final String PAYMENT_ID = "331541d6-617d-4464-b7d0-9b346b87f41c";

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ObjectMapper objectMapper;

  @LocalServerPort private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/playtomic/api/v1/wallets";
  }

  @Test
  @DisplayName("should create a wallet successfully")
  void should_create_a_wallet_successfully() throws Exception {
    CreateWalletRequest request = new CreateWalletRequest(CURRENCY_EUR);

    ResponseEntity<String> createResponse =
        restTemplate.postForEntity(getBaseUrl(), request, String.class);

    assertThat(createResponse.getStatusCode().value()).isEqualTo(200);
    assertThat(createResponse.getBody()).isNotNull();

    WalletSummaryResponse walletInfo =
        objectMapper.readValue(createResponse.getBody(), WalletSummaryResponse.class);
    assertThat(walletInfo.id()).isNotNull();
    assertThat(walletInfo.balance()).isEqualTo(new BigDecimal("0.00"));
    assertThat(walletInfo.currency()).isEqualTo(CURRENCY_EUR);

    String walletId = walletInfo.id().toString();

    ResponseEntity<String> getResponse =
        restTemplate.getForEntity(getBaseUrl() + "/" + walletId, String.class);

    assertThat(getResponse.getStatusCode().value()).isEqualTo(200);
    assertThat(getResponse.getBody()).isNotNull();

    WalletSummaryResponse retrievedWallet =
        objectMapper.readValue(getResponse.getBody(), WalletSummaryResponse.class);
    assertThat(retrievedWallet.id().toString()).isEqualTo(walletId);
    assertThat(retrievedWallet.balance()).isEqualTo(new BigDecimal("0.00"));
    assertThat(retrievedWallet.currency()).isEqualTo(CURRENCY_EUR);
  }

  @Test
  @DisplayName("should create wallets with different currencies")
  void should_create_wallets_with_different_currencies() throws Exception {
    CreateWalletRequest requestEur = new CreateWalletRequest(CURRENCY_EUR);
    CreateWalletRequest requestUsd = new CreateWalletRequest(CURRENCY_USD);

    ResponseEntity<WalletSummaryResponse> responseEur =
        restTemplate.postForEntity(getBaseUrl(), requestEur, WalletSummaryResponse.class);

    assertThat(responseEur.getStatusCode().value()).isEqualTo(200);
    assertThat(responseEur.getBody().currency()).isEqualTo(CURRENCY_EUR);

    ResponseEntity<WalletSummaryResponse> responseUsd =
        restTemplate.postForEntity(getBaseUrl(), requestUsd, WalletSummaryResponse.class);

    assertThat(responseUsd.getStatusCode().value()).isEqualTo(200);
    assertThat(responseUsd.getBody().currency()).isEqualTo(CURRENCY_USD);
  }

  @Test
  @DisplayName("should handle multiple top-ups to same wallet")
  void should_handle_multiple_top_ups_to_same_wallet() throws Exception {
    ResponseEntity<WalletSummaryResponse> createResponse = createWallet();

    assertThat(createResponse.getStatusCode().value()).isEqualTo(200);
    String walletId = createResponse.getBody().id().toString();

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/charges"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        String.format(
                            """
                            {
                              "id": "%s",
                              "amount": 50
                            }
                            """,
                            PAYMENT_ID))));

    TopUpRequest topUpRequest1 = new TopUpRequest(CREDIT_CARD, new BigDecimal("50.00"));
    ResponseEntity<TopUpResponse> topUpResponse1 =
        restTemplate.postForEntity(
            getBaseUrl() + "/" + walletId + "/top-up", topUpRequest1, TopUpResponse.class);

    assertThat(topUpResponse1.getStatusCode().value()).isEqualTo(200);

    TopUpRequest topUpRequest2 = new TopUpRequest(CREDIT_CARD, new BigDecimal("50.00"));
    ResponseEntity<TopUpResponse> topUpResponse2 =
        restTemplate.postForEntity(
            getBaseUrl() + "/" + walletId + "/top-up", topUpRequest2, TopUpResponse.class);

    assertThat(topUpResponse2.getStatusCode().value()).isEqualTo(200);

    Awaitility.await()
        .atMost(5, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> {
              ResponseEntity<WalletSummaryResponse> getResponse =
                  restTemplate.getForEntity(
                      getBaseUrl() + "/" + walletId, WalletSummaryResponse.class);

              assertThat(getResponse.getStatusCode().value()).isEqualTo(200);
              assertThat(getResponse.getBody().balance()).isEqualTo(new BigDecimal("100.00"));
            });
  }

  @Test
  @DisplayName("should handle stripe payment failures")
  void should_handle_stripe_payment_failures() throws Exception {
    ResponseEntity<WalletSummaryResponse> createResponse = createWallet();

    assertThat(createResponse.getStatusCode().value()).isEqualTo(200);
    String walletId = createResponse.getBody().id().toString();

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/charges"))
            .willReturn(aResponse().withStatus(422)));

    TopUpRequest topUpRequest = new TopUpRequest(CREDIT_CARD, new BigDecimal("5.00"));
    ResponseEntity<TopUpResponse> topUpResponse =
        restTemplate.postForEntity(
            getBaseUrl() + "/" + walletId + "/top-up", topUpRequest, TopUpResponse.class);

    assertThat(topUpResponse.getStatusCode().value()).isEqualTo(422);

    ResponseEntity<WalletSummaryResponse> getResponse =
        restTemplate.getForEntity(getBaseUrl() + "/" + walletId, WalletSummaryResponse.class);

    assertThat(getResponse.getStatusCode().value()).isEqualTo(200);
    assertThat(getResponse.getBody().balance()).isEqualTo("0.00");
  }

  private ResponseEntity<WalletSummaryResponse> createWallet() {
    CreateWalletRequest createRequest = new CreateWalletRequest(CURRENCY_EUR);
    return restTemplate.postForEntity(getBaseUrl(), createRequest, WalletSummaryResponse.class);
  }
}
