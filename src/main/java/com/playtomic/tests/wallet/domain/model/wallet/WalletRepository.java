package com.playtomic.tests.wallet.domain.model.wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {

  Optional<Wallet> findById(UUID id);

  Wallet save(Wallet wallet);
}
