package ru.t1.java.service1.service;


import ru.t1.java.service1.exception.TransactionException;


public interface TransactionService<T> {
    void applyAll(Iterable<T> entities) throws TransactionException;
}
