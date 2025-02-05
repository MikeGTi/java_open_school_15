package ru.t1.java.service1.service.impl.pre;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.service1.dto.TransactionAcceptedDto;
import ru.t1.java.service1.model.Account;
import ru.t1.java.service1.model.Transaction;
import ru.t1.java.service1.model.enums.AccountStatus;
import ru.t1.java.service1.model.enums.TransactionStatus;
import ru.t1.java.service1.repository.AccountRepository;
import ru.t1.java.service1.repository.TransactionRepository;
import ru.t1.java.service1.service.Rule;
import ru.t1.java.service1.util.TransactionMapper;

import java.util.UUID;

@Component
public class TransactionAccountOpenRule extends Rule<Transaction> {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<UUID, TransactionAcceptedDto> transactionAcceptedDtoProducer;

    @Value("${spring.kafka.topic.transaction-accept}")
    String topicToSend;

    public TransactionAccountOpenRule(TransactionRepository transactionRepository, TransactionMapper transactionMapper,
                                      AccountRepository accountRepository,
                                      KafkaTemplate<UUID, TransactionAcceptedDto> transactionAcceptedDtoProducer) {
        super(transaction -> transaction.getAccount().getStatus().equals(AccountStatus.OPEN));

        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.accountRepository = accountRepository;
        this.transactionAcceptedDtoProducer = transactionAcceptedDtoProducer;
    }

    @Override
    public boolean apply(Transaction transaction) {
        return super.apply(transaction);
    }

    @Override
    public boolean handle(Transaction transaction) {
        // save transaction wit new status
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction = transactionRepository.save(transaction);
        // set account new balance
        Account account = transaction.getAccount();
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        accountRepository.save(account);
        // kafka messaging
        UUID clientUuid = account.getClient().getClientUuid();
        TransactionAcceptedDto transactionAcceptedDto = transactionMapper.toAcceptedDto(transaction, account, clientUuid);
        transactionAcceptedDtoProducer.send(topicToSend, transactionAcceptedDto);
        return true;
    }
}

