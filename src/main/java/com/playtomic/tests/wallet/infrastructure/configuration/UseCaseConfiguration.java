package com.playtomic.tests.wallet.infrastructure.configuration;

import com.playtomic.tests.wallet.application.usecase.wallet.read.GetInfoUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.CreateWalletUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.ProcessPaymentUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.RefundPaymentUseCase;
import com.playtomic.tests.wallet.application.usecase.wallet.write.TopUpUseCase;
import com.playtomic.tests.wallet.domain.model.wallet.DomainEventBus;
import com.playtomic.tests.wallet.domain.model.wallet.WalletRepository;
import com.playtomic.tests.wallet.domain.model.wallet.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

  @Bean
  public GetInfoUseCase getInfoUseCase(final WalletRepository walletRepository) {
    return new GetInfoUseCase(walletRepository);
  }

  @Bean
  public CreateWalletUseCase createWalletUseCase(
      final WalletRepository walletRepository, final DomainEventBus eventBus) {
    return new CreateWalletUseCase(walletRepository, eventBus);
  }

  @Bean
  public ProcessPaymentUseCase processPaymentUseCase(
      final WalletRepository walletRepository, final DomainEventBus eventBus) {
    return new ProcessPaymentUseCase(walletRepository, eventBus);
  }

  @Bean
  public RefundPaymentUseCase refundPaymentUseCase(
      final WalletRepository walletRepository,
      final PaymentService paymentService,
      final DomainEventBus eventBus) {
    return new RefundPaymentUseCase(walletRepository, paymentService, eventBus);
  }

  @Bean
  public TopUpUseCase topUpUseCase(
      final WalletRepository walletRepository,
      final PaymentService paymentService,
      final DomainEventBus eventBus) {
    return new TopUpUseCase(walletRepository, paymentService, eventBus);
  }
}
