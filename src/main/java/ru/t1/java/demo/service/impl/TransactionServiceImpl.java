package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    @LogDataSourceError
    @Override
    public Transaction create(Transaction transaction) throws TransactionException {
        UUID accountUuid = transaction.getAccountUuid();
        Account account = accountRepository.findByAccountUuid(accountUuid)
                .orElseThrow(() -> new TransactionException(String.format("Account with uuid %s is not exists", accountUuid)));

        log.info("Balance of account uuid {} was {}", account.getAccountUuid(), account.getBalance());
        log.info("New transaction has amount {}", transaction.getAmount());

        account.setBalance(account.getBalance().add(transaction.getAmount()));

        log.info("New balance of account uuid {} are {} ", account.getAccountUuid(), account.getBalance());

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public Transaction findByUuid(UUID transactionUuid) {
        return transactionRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new TransactionException(String.format("Transaction with uuid %s is not exists", transactionUuid)));
    }

    @Override
    @Transactional(readOnly = true)
    @LogException
    @Track
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Transactional
    @LogDataSourceError
    @Override
    public Transaction update(UUID transactionUuid, TransactionDto transactionDto) throws TransactionException {
        Transaction transactionToUpdate = transactionRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new TransactionException(String.format("Transaction with uuid %s is not exists", transactionUuid)));
        Account account = accountRepository.findByAccountUuid(transactionToUpdate.getAccountUuid())
                .orElseThrow(() -> new TransactionException(String.format("Account with uuid %s is not exists", transactionToUpdate.getAccountUuid())));

        transactionToUpdate.setAmount(transactionDto.getAmount());
        transactionToUpdate.setCreated(transactionDto.getCreated());
        transactionToUpdate.setAccount(account);

        return transactionRepository.save(transactionToUpdate);
    }

    @Transactional
    @LogDataSourceError
    @Override
    public void delete(UUID transactionUuid) throws TransactionException {
        transactionRepository.delete(transactionRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new TransactionException(String.format("Transaction with uuid %s is not exists", transactionUuid))));
    }
}

