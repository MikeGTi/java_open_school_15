package ru.t1.java.saver.service;

import ru.t1.java.saver.exception.AccountException;
import ru.t1.java.saver.model.Account;

public interface AccountService {

    void save(Account accounts) throws AccountException;

    void saveAll(Iterable<Account> accounts) throws AccountException;
}