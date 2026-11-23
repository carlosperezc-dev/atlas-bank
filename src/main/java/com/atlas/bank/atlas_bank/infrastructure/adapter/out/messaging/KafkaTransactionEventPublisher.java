package com.atlas.bank.atlas_bank.infrastructure.adapter.out.messaging;

import com.atlas.bank.atlas_bank.application.port.out.TransactionEventPublisher;
import com.atlas.bank.atlas_bank.domain.event.TransactionExecutedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionEventPublisher implements TransactionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionEventPublisher.class);
    private final KafkaTemplate<String,TransactionExecutedEvent> kafkaTemplate;
    private final String topic;

    public KafkaTransactionEventPublisher(KafkaTemplate<String,TransactionExecutedEvent> kafkaTemplate,
                                          @Value("${app.kafka.topics.transaction-events}") String topic){
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }


    @Override
    public void publish(TransactionExecutedEvent event) {
        String key = event.transactionId().toString();

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result,exception) ->{
                    if (exception!=null){
                        log.error("Error publishing transaction event: {}",
                                event.transactionId(),
                                exception);
                        return;
                    }
                    log.info(
                            "Transaction event published. TransactionId: {}, Partition {}, Offset {}",
                            event.transactionId().toString(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );

                });
    }

}
