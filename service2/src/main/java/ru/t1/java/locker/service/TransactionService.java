package ru.t1.java.locker.service;


import ru.t1.java.locker.exception.TransactionException;


public interface TransactionService<T> {
    void applyAll(Iterable<T> entities) throws TransactionException;
}
