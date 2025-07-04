package com.playtomic.tests.wallet.domain.model.wallet;

public interface DomainEventBus {

  void publishDomainEvent(Object event);
}
