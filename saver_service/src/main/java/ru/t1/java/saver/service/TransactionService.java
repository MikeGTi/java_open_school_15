package ru.t1.java.saver.service;


import ru.t1.java.saver.exception.TransactionException;
import ru.t1.java.saver.model.Transaction;


public interface TransactionService {

    void save(Transaction transaction) throws TransactionException;

    void saveAll(Iterable<Transaction> transactions) throws TransactionException;
}
