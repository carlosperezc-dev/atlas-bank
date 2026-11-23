package com.atlas.bank.atlas_bank.infrastructure.adapter.listener;

import com.atlas.bank.atlas_bank.application.port.out.TransactionEventPublisher;
import com.atlas.bank.atlas_bank.domain.event.TransactionExecutedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionDomainEventListener {

    private final TransactionEventPublisher transactionEventPublisher;

    public TransactionDomainEventListener(TransactionEventPublisher transactionEventPublisher){
        this.transactionEventPublisher = transactionEventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TransactionExecutedEvent event){
        transactionEventPublisher.publish(event);
    }
}
