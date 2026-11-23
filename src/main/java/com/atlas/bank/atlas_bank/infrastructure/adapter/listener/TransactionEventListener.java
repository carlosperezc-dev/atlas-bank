package com.atlas.bank.atlas_bank.infrastructure.adapter.listener;

import com.atlas.bank.atlas_bank.domain.event.TransactionExecutedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {
    private static final Logger log = LoggerFactory.getLogger(TransactionEventListener.class);

    @KafkaListener(
            topics = "${app.kafka.topics.transaction-events}",
            groupId = "atlas-bank-group"
    )

    public void consume(TransactionExecutedEvent event){
        log.info(
                "Transaction event received: transactionId = {}, type = {}, amount= {}, fee= {}",
                event.transactionId(),
                event.type(),
                event.amount(),
                event.fee()
        );
    }

}
