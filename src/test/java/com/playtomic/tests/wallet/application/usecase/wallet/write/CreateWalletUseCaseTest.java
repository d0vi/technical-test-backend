package com.playtomic.tests.wallet.application.usecase.wallet.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletCreated;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateWalletUseCaseTest {

  private static final String CURRENCY_EUR = "EUR";

  @Mock private WalletRepository walletRepository;
  @Mock private DomainEventBus domainEventBus;

  @InjectMocks private CreateWalletUseCase createWalletUseCase;

  @Test
  @DisplayName("should create a wallet")
  void should_create_a_wallet() {
    Wallet expectedWallet = new Wallet(CURRENCY_EUR);
    when(this.walletRepository.save(any(Wallet.class))).thenReturn(expectedWallet);

    Wallet result = this.createWalletUseCase.execute(CURRENCY_EUR);

    assertThat(result).isEqualTo(expectedWallet);
    assertThat(result.id()).isNotNull();
    assertThat(result.balance().compareTo(BigDecimal.ZERO)).isEqualTo(0);
    assertThat(result.transactions()).isEmpty();
    verify(this.walletRepository)
        .save(
            argThat(
                wallet ->
                    wallet.id() != null
                        && wallet.balance().compareTo(BigDecimal.ZERO) == 0
                        && wallet.transactions().isEmpty()));
  }

  @Test
  @DisplayName("should publish a wallet created event")
  void should_publish_a_wallet_created_event() {
    Wallet savedWallet = new Wallet(CURRENCY_EUR);
    when(this.walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

    this.createWalletUseCase.execute(CURRENCY_EUR);

    verify(this.domainEventBus)
        .publishDomainEvent(
            argThat(
                event ->
                    event instanceof WalletCreated && ((WalletCreated) event).walletId() != null));
  }
}
