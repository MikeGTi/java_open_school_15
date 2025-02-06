package ru.t1.java.locker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.locker.dto.TransactionResultDto;
import ru.t1.java.locker.model.Transaction;
import ru.t1.java.locker.service.Rule;
import ru.t1.java.locker.util.TransactionMapper;

import java.util.UUID;

@Component
@Slf4j
@Order
public class TransactionAcceptedRule extends Rule<Transaction> {

    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<UUID, TransactionResultDto> transactionResultDtoProducer;

    @Value("${spring.kafka.topic.transaction-result}")
    String topicToSend;

    public TransactionAcceptedRule(TransactionMapper transactionMapper, KafkaTemplate<UUID, TransactionResultDto> transactionResultDtoProducer) {
        super(transactionAcceptedDto ->  true);
        this.transactionMapper = transactionMapper;
        this.transactionResultDtoProducer = transactionResultDtoProducer;
    }

    @Override
    public boolean apply(Transaction transaction) {
        return super.apply(transaction);
    }

    @Override
    public boolean handle(Transaction transaction) {
        // kafka messaging
        transactionResultDtoProducer.send(topicToSend, transactionMapper.toResultDto(transaction));
        return true;
    }
}

