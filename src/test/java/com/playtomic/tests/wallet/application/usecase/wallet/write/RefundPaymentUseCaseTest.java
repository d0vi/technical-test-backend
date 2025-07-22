package com.playtomic.tests.wallet.application.usecase.wallet.write;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.Wallet;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.event.PaymentRefunded;
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
class RefundPaymentUseCaseTest {

  @Mock private WalletRepository walletRepository;
  @Mock private PaymentService paymentService;
  @Mock private DomainEventBus domainEventBus;

  @InjectMocks private RefundPaymentUseCase refundPaymentUseCase;

  @Test
  @DisplayName("should refund a payment")
  void should_refund_a_payment() {
    UUID walletId = UUID.randomUUID();
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";
    BigDecimal amount = new BigDecimal("100.00");
    Wallet wallet = new WalletTestBuilder().withId(walletId).build();
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

    this.refundPaymentUseCase.execute(walletId, paymentId, amount);

    verify(this.paymentService).refund(paymentId);
  }

  @Test
  @DisplayName("should throw an exception when the wallet does not exist")
  void should_throw_an_exception_when_the_wallet_does_not_exist() {
    UUID walletId = UUID.randomUUID();
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";
    BigDecimal amount = new BigDecimal("200.00");
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.refundPaymentUseCase.execute(walletId, paymentId, amount))
        .isInstanceOf(UnknownWalletIdException.class)
        .hasMessage("Wallet " + walletId + " can not be found");
  }

  @Test
  @DisplayName("should publish a payment refunded event")
  void should_publish_a_payment_refunded_event() {
    UUID walletId = UUID.randomUUID();
    String paymentId = "d9183c7d-a682-47be-9817-96d3627539ee";
    BigDecimal amount = new BigDecimal("200.00");
    Wallet wallet = new WalletTestBuilder().withId(walletId).build();
    when(this.walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

    this.refundPaymentUseCase.execute(walletId, paymentId, amount);

    verify(this.domainEventBus)
        .publishDomainEvent(
            argThat(
                event ->
                    event instanceof PaymentRefunded
                        && ((PaymentRefunded) event).walletId().equals(walletId)
                        && ((PaymentRefunded) event).paymentId().equals(paymentId)
                        && ((PaymentRefunded) event).amount().equals(amount)
                        && ((PaymentRefunded) event).currency().equals(wallet.currency())));
  }
}
