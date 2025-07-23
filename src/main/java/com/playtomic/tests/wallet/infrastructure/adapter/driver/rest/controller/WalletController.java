package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller;

import com.playtomic.tests.wallet.application.usecase.transaction.read.GetTransactionsUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.read.GetInfoUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.CreateWalletUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.TopUpUseCase;
import com.playtomic.tests.wallet.domain.model.transaction.Page;
import com.playtomic.tests.wallet.domain.model.transaction.Transaction;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request.CreateWalletRequest;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.request.TopUpRequest;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response.TopUpResponse;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response.TransactionsResponse;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response.WalletSummaryResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

  private final CreateWalletUseCase createWalletUseCase;
  private final GetInfoUseCase getInfoUseCase;
  private final GetTransactionsUseCase getTransactionsUseCase;
  private final TopUpUseCase topUpUseCase;

  public WalletController(
      CreateWalletUseCase createWalletUseCase,
      GetInfoUseCase getInfoUseCase,
      GetTransactionsUseCase getTransactionsUseCase,
      TopUpUseCase topUpUseCase) {
    this.createWalletUseCase = createWalletUseCase;
    this.getInfoUseCase = getInfoUseCase;
    this.getTransactionsUseCase = getTransactionsUseCase;
    this.topUpUseCase = topUpUseCase;
  }

  @GetMapping("/{id}")
  public ResponseEntity<WalletSummaryResponse> getWallet(@PathVariable("id") UUID walletId) {
    Wallet wallet = this.getInfoUseCase.execute(walletId);
    return ResponseEntity.ok(this.toWalletResponse(wallet));
  }

  @PostMapping
  public ResponseEntity<WalletSummaryResponse> createWallet(
      @Valid @RequestBody CreateWalletRequest request) {
    Wallet wallet = this.createWalletUseCase.execute(request.currencyCode());
    return ResponseEntity.ok(this.toWalletResponse(wallet));
  }

  @PostMapping("/{id}/top-up")
  public ResponseEntity<TopUpResponse> topUpWallet(
      @PathVariable("id") UUID walletId, @Valid @RequestBody TopUpRequest request) {
    String paymentId = this.topUpUseCase.execute(walletId, request.creditCard(), request.amount());
    return ResponseEntity.ok(new TopUpResponse(paymentId));
  }

  @GetMapping("/{id}/transactions")
  public ResponseEntity<TransactionsResponse> getWalletTransactions(
      @PathVariable("id") UUID walletId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<Transaction> transactionsPage = this.getTransactionsUseCase.execute(walletId, page, size);
    return ResponseEntity.ok(this.toTransactionsResponse(transactionsPage));
  }

  private WalletSummaryResponse toWalletResponse(Wallet wallet) {
    return new WalletSummaryResponse(wallet.id(), wallet.balance(), wallet.currency());
  }

  private TransactionsResponse toTransactionsResponse(Page<Transaction> transactionsPage) {
    List<TransactionsResponse.TransactionResponse> transactions =
        transactionsPage.content().stream()
            .map(
                t ->
                    new TransactionsResponse.TransactionResponse(
                        t.id(), t.type().toString(), t.amount(), t.paymentId(), t.createdAt()))
            .toList();
    return new TransactionsResponse(
        transactions,
        transactionsPage.currentPage(),
        transactionsPage.totalPages(),
        transactionsPage.totalElements(),
        transactionsPage.hasNext(),
        transactionsPage.hasPrevious());
  }
}
