package com.playtomic.tests.wallet.application.usecase.wallet.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import com.playtomic.tests.wallet.helper.WalletTestBuilder;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetInfoUseCaseTest {

  @Mock private WalletRepository walletRepository;

  @InjectMocks private GetInfoUseCase getInfoUseCase;

  @Test
  @DisplayName("should return the wallet")
  void should_return_the_wallet() {
    UUID walletId = UUID.randomUUID();
    Wallet expectedWallet = new WalletTestBuilder().withId(walletId).withBalance("100.50").build();
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.of(expectedWallet));

    Wallet result = this.getInfoUseCase.execute(walletId);

    assertThat(result).isEqualTo(expectedWallet);
    assertThat(result.id()).isEqualTo(walletId);
    assertThat(result.balance()).isEqualTo(new BigDecimal("100.50"));
  }

  @Test
  @DisplayName("should throw an exception when the wallet does not exist")
  void should_throw_an_exception_when_the_wallet_does_not_exist() {
    UUID walletId = UUID.randomUUID();
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.getInfoUseCase.execute(walletId))
        .isInstanceOf(UnknownWalletIdException.class)
        .hasMessage("Could not found wallet with UUID '" + walletId + "'");
  }
}
