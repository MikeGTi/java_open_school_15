package ru.t1.java.service1.service;

import ru.t1.java.service1.exception.AccountException;
import ru.t1.java.service1.model.Account;

public interface AccountService {
    void saveAll(Iterable<Account> accounts) throws AccountException;
}