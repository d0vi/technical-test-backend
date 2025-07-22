package com.playtomic.tests.wallet.application.usecase.wallet.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentCreated;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import com.playtomic.tests.wallet.domain.model.wallet.service.PaymentService;
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
class TopUpUseCaseTest {

  @Mock private WalletRepository walletRepository;
  @Mock private PaymentService paymentService;
  @Mock private DomainEventBus domainEventBus;

  @InjectMocks private TopUpUseCase topUpUseCase;

  @Test
  @DisplayName("should process a top up")
  void should_process_a_top_up() {
    UUID walletId = UUID.randomUUID();
    String creditCard = "4242424242424242";
    BigDecimal amount = new BigDecimal("100.00");
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";
    Wallet existingWallet = new WalletTestBuilder().withId(walletId).build();
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.of(existingWallet));
    when(this.paymentService.charge(creditCard, amount)).thenReturn(paymentId);

    String result = this.topUpUseCase.execute(walletId, creditCard, amount);

    assertThat(result).isEqualTo(paymentId);
    verify(this.paymentService).charge(creditCard, amount);
  }

  @Test
  @DisplayName("should throw an exception when the wallet does not exist")
  void should_throw_an_exception_when_the_wallet_does_not_exist() {
    UUID walletId = UUID.randomUUID();
    String creditCard = "4242424242424242";
    BigDecimal amount = new BigDecimal("100.00");

    when(this.walletRepository.findById(walletId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.topUpUseCase.execute(walletId, creditCard, amount))
        .isInstanceOf(UnknownWalletIdException.class)
        .hasMessage("Wallet " + walletId + " can not be found");
  }

  @Test
  @DisplayName("should publish a payment created event")
  void should_publish_a_payment_created_event() {
    UUID walletId = UUID.randomUUID();
    String creditCard = "4242424242424242";
    BigDecimal amount = new BigDecimal("200.00");
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";

    Wallet existingWallet = new Wallet("EUR");
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.of(existingWallet));
    when(this.paymentService.charge(creditCard, amount)).thenReturn(paymentId);

    this.topUpUseCase.execute(walletId, creditCard, amount);

    verify(this.domainEventBus)
        .publishDomainEvent(
            argThat(
                event ->
                    event instanceof PaymentCreated
                        && ((PaymentCreated) event).walletId().equals(walletId)
                        && ((PaymentCreated) event).paymentId().equals(paymentId)
                        && ((PaymentCreated) event).amount().equals(amount)));
  }
}
