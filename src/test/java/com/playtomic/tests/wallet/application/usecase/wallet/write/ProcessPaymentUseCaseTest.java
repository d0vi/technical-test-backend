package com.playtomic.tests.wallet.application.usecase.wallet.write;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.WalletToppedUp;
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
class ProcessPaymentUseCaseTest {

  @Mock private WalletRepository walletRepository;
  @Mock private DomainEventBus domainEventBus;

  @InjectMocks private ProcessPaymentUseCase processPaymentUseCase;

  @Test
  @DisplayName("should process a payment")
  void should_process_a_payment() {
    UUID walletId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("100.00");
    BigDecimal initialBalance = new BigDecimal("50.00");
    Wallet wallet = new WalletTestBuilder().withId(walletId).withBalance(initialBalance).build();
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

    this.processPaymentUseCase.execute(walletId, "d9183c7d-a682-47be-9817-96d3627539ee", amount);

    verify(this.walletRepository)
        .save(
            argThat(
                savedWallet ->
                    savedWallet.balance().equals(initialBalance.add(amount))
                        && savedWallet.transactions().size() == 1
                        && savedWallet.updatedAt() != null));
  }

  @Test
  @DisplayName("should throw an exception when the wallet does not exist")
  void should_throw_an_exception_when_the_wallet_does_not_exist() {
    UUID walletId = UUID.randomUUID();
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                this.processPaymentUseCase.execute(
                    walletId, "d9183c7d-a682-47be-9817-96d3627539ee", new BigDecimal("100.00")))
        .isInstanceOf(UnknownWalletIdException.class)
        .hasMessage("Wallet " + walletId + " can not be found");
  }

  @Test
  @DisplayName("should publish a wallet topped up event")
  void should_publish_a_wallet_topped_up_event() {
    UUID walletId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("75.50");
    BigDecimal initialBalance = new BigDecimal("25.00");
    String currency = "USD";
    Wallet wallet =
        new WalletTestBuilder()
            .withId(walletId)
            .withBalance(initialBalance)
            .withCurrency(currency)
            .build();
    when(this.walletRepository.findById(any())).thenReturn(Optional.of(wallet));

    this.processPaymentUseCase.execute(walletId, "d9183c7d-a682-47be-9817-96d3627539ee", amount);

    verify(this.domainEventBus)
        .publishDomainEvent(
            argThat(
                event ->
                    event instanceof WalletToppedUp
                        && ((WalletToppedUp) event).walletId().equals(walletId)
                        && ((WalletToppedUp) event).amount().equals(amount)
                        && ((WalletToppedUp) event).previousBalance().equals(initialBalance)
                        && ((WalletToppedUp) event).newBalance().equals(initialBalance.add(amount))
                        && ((WalletToppedUp) event).currency().equals(currency)));
  }
}
