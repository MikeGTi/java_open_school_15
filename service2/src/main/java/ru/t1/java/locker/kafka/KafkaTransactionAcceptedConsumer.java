package ru.t1.java.locker.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.locker.dto.TransactionAcceptedDto;
import ru.t1.java.locker.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionAcceptedConsumer<T extends TransactionAcceptedDto> {

    private final TransactionService<TransactionAcceptedDto> transactionRuleApplierService;

    @KafkaListener(id = "kafkaTransactionAcceptedListener",
                   topics = "${spring.kafka.topic.transaction-accept}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<T> messageList,
                                  Acknowledgment ack) {

        log.debug("Accepted transaction consumer: Обработка новых сообщений");
        try {
            List<TransactionAcceptedDto> transactions = messageList.stream()
                                                                   .map(obj -> (TransactionAcceptedDto) obj)
                                                                   .toList();
            transactionRuleApplierService.applyAll(transactions);
        } finally {
            ack.acknowledge();
        }
        log.debug("Accepted transaction consumer: записи обработаны");
    }
}
