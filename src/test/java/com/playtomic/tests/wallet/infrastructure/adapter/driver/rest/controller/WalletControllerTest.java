package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.application.usecase.transaction.read.GetTransactionsUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.read.GetInfoUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.CreateWalletUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.TopUpUseCase;
import com.playtomic.tests.wallet.domain.model.transaction.Page;
import com.playtomic.tests.wallet.domain.model.transaction.Transaction;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import com.playtomic.tests.wallet.helper.WalletTestBuilder;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request.CreateWalletRequest;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request.TopUpRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

  private static final String CURRENCY_EUR = "EUR";
  private static final String WALLET_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String CREDIT_CARD = "4242424242424242";
  private static final String PAYMENT_ID = "331541d6-617d-4464-b7d0-9b346b87f41c";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreateWalletUseCase createWalletUseCase;

  @MockitoBean private GetInfoUseCase getInfoUseCase;

  @MockitoBean private GetTransactionsUseCase getTransactionsUseCase;

  @MockitoBean private TopUpUseCase topUpUseCase;

  @Test
  @DisplayName("should create a wallet")
  void should_create_a_wallet() throws Exception {
    Wallet wallet = new WalletTestBuilder().build();
    when(createWalletUseCase.execute(CURRENCY_EUR)).thenReturn(wallet);

    ResultActions result =
        mockMvc.perform(
            post("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateWalletRequest(CURRENCY_EUR))));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$.id").value(wallet.id().toString()),
        jsonPath("$.balance").value(wallet.balance().doubleValue()),
        jsonPath("$.currency").value(wallet.currency()));
  }

  @ParameterizedTest(name = "when {1}")
  @CsvSource({",the currency is null", "'',the currency is empty"})
  @DisplayName("should return bad request")
  void should_return_bad_request(String currency, String reason) throws Exception {
    CreateWalletRequest request = new CreateWalletRequest(currency);

    ResultActions result =
        mockMvc.perform(
            post("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

    result.andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should get a wallet's info")
  void should_get_a_wallets_info() throws Exception {
    UUID walletId = UUID.fromString(WALLET_ID);
    Wallet wallet = new WalletTestBuilder().withId(walletId).build();
    when(getInfoUseCase.execute(walletId)).thenReturn(wallet);

    ResultActions result = mockMvc.perform(get("/api/v1/wallets/{id}", WALLET_ID));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$.id").value(wallet.id().toString()),
        jsonPath("$.balance").value(wallet.balance().doubleValue()),
        jsonPath("$.currency").value(wallet.currency()));
  }

  @Test
  @DisplayName("should return not found when a wallet does not exist")
  void should_return_not_found_when_a_wallet_does_not_exist() throws Exception {
    UUID walletId = UUID.fromString(WALLET_ID);
    when(getInfoUseCase.execute(walletId)).thenThrow(new UnknownWalletIdException(walletId));

    ResultActions result = mockMvc.perform(get("/api/v1/wallets/{id}", WALLET_ID));

    result.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("should top up a wallet")
  void should_top_up_a_wallet() throws Exception {
    UUID walletId = UUID.fromString(WALLET_ID);
    TopUpRequest request = new TopUpRequest(CREDIT_CARD, new BigDecimal("100.00"));
    when(topUpUseCase.execute(walletId, CREDIT_CARD, new BigDecimal("100.00")))
        .thenReturn(PAYMENT_ID);

    ResultActions result =
        mockMvc.perform(
            post("/api/v1/wallets/{id}/top-up", WALLET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

    result.andExpectAll(status().isOk(), jsonPath("$.paymentId").value(PAYMENT_ID));
  }

  @ParameterizedTest(name = "when {2}")
  @MethodSource("provideInvalidTopUpRequests")
  @DisplayName("should return bad request")
  void should_return_bad_request(String creditCard, BigDecimal amount, String reason)
      throws Exception {
    TopUpRequest request = new TopUpRequest(creditCard, amount);

    ResultActions result =
        mockMvc.perform(
            post("/api/v1/wallets/{id}/top-up", WALLET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

    result.andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return bad request when a wallet id is invalid")
  void should_return_bad_request_when_a_wallet_id_is_invalid() throws Exception {
    TopUpRequest request = new TopUpRequest(CREDIT_CARD, new BigDecimal("100.00"));

    ResultActions result =
        mockMvc.perform(
            post("/api/v1/wallets/{id}/top-up", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

    result.andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should get wallet transactions")
  void should_get_wallet_transactions() throws Exception {
    UUID walletId = UUID.fromString(WALLET_ID);
    when(getTransactionsUseCase.execute(walletId, 0, 10))
        .thenReturn(
            new Page<>(
                List.of(
                    new Transaction(walletId.toString(), new BigDecimal("50.00"), PAYMENT_ID),
                    new Transaction(
                        walletId.toString(),
                        new BigDecimal("25.00"),
                        UUID.randomUUID().toString())),
                0,
                1,
                2,
                false,
                false));

    ResultActions result =
        mockMvc.perform(
            get("/api/v1/wallets/{id}/transactions", WALLET_ID)
                .param("page", "0")
                .param("size", "10"));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$.transactions").isArray(),
        jsonPath("$.transactions.length()").value(2),
        jsonPath("$.transactions[0].amount").value(50.00),
        jsonPath("$.transactions[1].amount").value(25.00),
        jsonPath("$.transactions[0].type").value("DEPOSIT"),
        jsonPath("$.transactions[1].type").value("DEPOSIT"),
        jsonPath("$.transactions[0].paymentId").value(PAYMENT_ID),
        jsonPath("$.currentPage").value(0),
        jsonPath("$.totalPages").value(1),
        jsonPath("$.totalElements").value(2),
        jsonPath("$.hasNext").value(false),
        jsonPath("$.hasPrevious").value(false));
  }

  @Test
  @DisplayName("should get wallet transactions with default pagination")
  void should_get_wallet_transactions_with_default_pagination() throws Exception {
    UUID walletId = UUID.fromString(WALLET_ID);
    when(getTransactionsUseCase.execute(walletId, 0, 10))
        .thenReturn(new Page<>(List.of(), 0, 0, 0, false, false));

    ResultActions result = mockMvc.perform(get("/api/v1/wallets/{id}/transactions", WALLET_ID));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$.transactions").isArray(),
        jsonPath("$.transactions.length()").value(0),
        jsonPath("$.currentPage").value(0),
        jsonPath("$.totalPages").value(0),
        jsonPath("$.totalElements").value(0));
  }

  private static Stream<Arguments> provideInvalidTopUpRequests() {
    return Stream.of(
        Arguments.of("", new BigDecimal("100.00"), "the credit card is blank"),
        Arguments.of(CREDIT_CARD, null, "the amount is null"),
        Arguments.of(CREDIT_CARD, new BigDecimal("-10.00"), "the amount is negative"),
        Arguments.of(CREDIT_CARD, BigDecimal.ZERO, "the amount is zero"));
  }
}
