package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller;

import com.playtomic.tests.wallet.application.usecase.wallet.read.GetInfoUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.CreateWalletUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.TopUpUseCase;
import com.playtomic.tests.wallet.domain.model.wallet.Transaction;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.TopUpRequest;
import com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.WalletInfoResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

  private final CreateWalletUseCase createWalletUseCase;
  private final GetInfoUseCase getInfoUseCase;
  private final TopUpUseCase topUpUseCase;

  public WalletController(
      CreateWalletUseCase createWalletUseCase,
      GetInfoUseCase getInfoUseCase,
      TopUpUseCase topUpUseCase) {
    this.createWalletUseCase = createWalletUseCase;
    this.getInfoUseCase = getInfoUseCase;
    this.topUpUseCase = topUpUseCase;
  }

  @GetMapping("/{id}")
  public ResponseEntity<WalletInfoResponse> getWallet(@PathVariable("id") UUID walletId) {
    Wallet wallet = this.getInfoUseCase.execute(walletId);
    return ResponseEntity.ok(this.toWalletResponse(wallet));
  }

  @PostMapping
  public ResponseEntity<WalletInfoResponse> createWallet(
      @Valid @RequestBody CreateWalletRequest request) {
    Wallet wallet = this.createWalletUseCase.execute(request.currencyCode());
    return ResponseEntity.ok(this.toWalletResponse(wallet));
  }

  @PostMapping("/{id}/top-up")
  public ResponseEntity<String> topUpWallet(
      @PathVariable("id") UUID walletId, @Valid @RequestBody TopUpRequest request) {
    String paymentId = this.topUpUseCase.execute(walletId, request.creditCard(), request.amount());
    return ResponseEntity.ok(paymentId);
  }

  private WalletInfoResponse toWalletResponse(Wallet wallet) {
    return new WalletInfoResponse(
        wallet.id(),
        wallet.balance(),
        wallet.currency(),
        this.toTransactionResponse(wallet.transactions()));
  }

  private List<WalletInfoResponse.TransactionResponse> toTransactionResponse(
      List<Transaction> transactions) {
    return transactions.stream()
        .map(
            t ->
                new WalletInfoResponse.TransactionResponse(
                    t.id(), t.type().toString(), t.amount(), t.paymentId(), t.createdAt()))
        .toList();
  }
}
