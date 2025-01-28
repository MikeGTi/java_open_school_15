package ru.t1.java.saver.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.saver.dto.TransactionDto;
import ru.t1.java.saver.model.Transaction;
import ru.t1.java.saver.service.TransactionService;
import ru.t1.java.saver.util.TransactionMapper;


import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionConsumer<T extends TransactionDto> {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @KafkaListener(id = "kafkaTransactionListener",
                   topics = "${spring.kafka.topic.transaction-accept}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<T> messageList,
                                  Acknowledgment ack) {

        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            List<Transaction> transactions = messageList.stream()
                                              .map(transactionMapper::toEntity)
                                              .toList();
            transactionService.saveAll(transactions);
        } finally {
            ack.acknowledge();
        }
        log.debug("Transaction consumer: записи обработаны");
    }
}
