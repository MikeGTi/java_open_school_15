package ru.t1.java.base.service;

import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.base.dto.TransactionDto;
import ru.t1.java.base.exception.TransactionException;
import ru.t1.java.base.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    @Transactional
    Transaction create(Transaction transaction) throws TransactionException;

    @Transactional(readOnly = true)
    Transaction findByUuid(UUID uuid);

    @Transactional(readOnly = true)
    List<Transaction> findAll();

    @Transactional
    Transaction update(UUID transactionUuid, TransactionDto transactionDto) throws TransactionException;

    @Transactional
    void delete(UUID transactionUuid) throws TransactionException;
}
