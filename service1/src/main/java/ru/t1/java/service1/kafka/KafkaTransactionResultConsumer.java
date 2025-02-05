package ru.t1.java.service1.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.service1.dto.TransactionResultDto;
import ru.t1.java.service1.service.TransactionService;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionResultConsumer<T extends TransactionResultDto> {

    private final TransactionService<TransactionResultDto> transactionRuleApplierAfterService;

    @KafkaListener(id = "kafkaTransactionResultListener",
                   topics = "${spring.kafka.topic.transaction-result}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<T> messageList,
                                  Acknowledgment ack) {

        log.debug("Transaction result consumer: Обработка новых сообщений");
        try {
            List<TransactionResultDto> transactions = messageList.stream()
                                                                 .map(obj -> (TransactionResultDto) obj)
                                                                 .toList();
            transactionRuleApplierAfterService.applyAll(transactions);
        } finally {
            ack.acknowledge();
        }
        log.debug("Transaction result consumer: Записи обработаны");
    }
}
