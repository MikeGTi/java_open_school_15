package ru.t1.java.base.service;

import ru.t1.java.base.exception.AccountException;
import ru.t1.java.base.model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    Account create(Account account);

    List<Account> findAll();

    Account findByAccountUuid(UUID accountUuid) throws AccountException;

    List<Account> findAccountsByAccountUuid(UUID accountUuid) throws AccountException;

    Account update(UUID accountUuid, Account accountDto) throws AccountException;

    void delete(UUID accountUuid) throws AccountException;
}