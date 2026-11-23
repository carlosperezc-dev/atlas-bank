package com.atlas.bank.atlas_bank.application.port.out;

import com.atlas.bank.atlas_bank.domain.event.TransactionExecutedEvent;

public interface TransactionEventPublisher {
    void publish (TransactionExecutedEvent event);
}
