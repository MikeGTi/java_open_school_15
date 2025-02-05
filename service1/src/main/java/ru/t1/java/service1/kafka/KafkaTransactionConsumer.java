package ru.t1.java.service1.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.service1.dto.TransactionDto;
import ru.t1.java.service1.model.Transaction;
import ru.t1.java.service1.service.TransactionService;
import ru.t1.java.service1.util.TransactionMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionConsumer<T extends TransactionDto> {

    private final TransactionService<Transaction> transactionRuleApplierPreService;
    private final TransactionMapper transactionMapper;

    @KafkaListener(id = "kafkaTransactionListener",
                   topics = "${spring.kafka.topic.transaction-registration}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<T> messageList,
                                  Acknowledgment ack) {

        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            List<Transaction> transactions = messageList.stream()
                                              .map(transactionMapper::toEntity)
                                              .toList();
            transactionRuleApplierPreService.applyAll(transactions);
        } finally {
            ack.acknowledge();
        }
        log.debug("Transaction consumer: Записи обработаны");
    }
}
