package ru.t1.java.locker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.locker.dto.TransactionResultDto;
import ru.t1.java.locker.model.Transaction;
import ru.t1.java.locker.model.enums.TransactionStatus;
import ru.t1.java.locker.repository.TransactionRepository;
import ru.t1.java.locker.service.Rule;
import ru.t1.java.locker.util.TransactionMapper;

import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Component
@Slf4j
@Order(2)
public class TransactionBlockedRule extends Rule<Transaction> {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<UUID, TransactionResultDto> transactionResultDtoProducer;

    @Value("${spring.kafka.topic.transaction-result}")
    String topicToSend;

    @Value("${t1.transaction.perform-period-min-ms}")
    private Period performPeriod;

    private final Predicate<Transaction> predicate;

    public TransactionBlockedRule(TransactionRepository transactionRepository, TransactionMapper transactionMapper, KafkaTemplate<UUID, TransactionResultDto> transactionResultDtoProducer) {
        super(t -> false);
        this.predicate = (transaction -> {
                            List<Transaction> transactions = transactionRepository.findAllTransactionsByCreatedBetween(transaction.getCreated().minus(performPeriod),
                                                                                                                       transaction.getCreated());
                            return transactions.size() > 1;
                         });
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionResultDtoProducer = transactionResultDtoProducer;
    }

    @Override
    public boolean apply(Transaction transaction) {
        return this.predicate.test(transaction);
    }

    @Override
    public boolean handle(Transaction transaction) {
        transaction.setStatus(TransactionStatus.BLOCKED);
        // kafka messaging
        transactionResultDtoProducer.send(topicToSend, transactionMapper.toResultDto(transaction));
        return true;
    }
}