package com.playtomic.tests.wallet.application.usecase.wallet.read;

import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import java.util.UUID;

public class GetInfoUseCase {

  private final WalletRepository repository;

  public GetInfoUseCase(WalletRepository repository) {
    this.repository = repository;
  }

  public Wallet execute(UUID walletId) {
    return this.repository
        .findById(walletId)
        .orElseThrow(() -> new UnknownWalletIdException(walletId));
  }
}
