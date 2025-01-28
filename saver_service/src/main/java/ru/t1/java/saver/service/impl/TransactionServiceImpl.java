package ru.t1.java.saver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.t1.java.saver.model.Transaction;
import ru.t1.java.saver.repository.TransactionRepository;
import ru.t1.java.saver.service.TransactionService;


@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void saveAll(Iterable<Transaction> transactions) {
        transactionRepository.saveAllAndFlush(transactions);
    }
}

