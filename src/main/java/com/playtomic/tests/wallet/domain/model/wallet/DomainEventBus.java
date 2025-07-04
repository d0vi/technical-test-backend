package com.playtomic.tests.wallet.domain.model.wallet;

import com.playtomic.tests.wallet.domain.model.wallet.event.Event;

public interface DomainEventBus {

  void publishDomainEvent(Event event);
}
