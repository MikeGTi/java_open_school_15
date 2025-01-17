package ru.t1.java.demo.service;

import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    @Transactional(readOnly = true)
    @LogDataSourceError
    Account getAccountsByAccountUuid(UUID accountUuid);

    @Transactional(readOnly = true)
    @LogDataSourceError
    List<Account> getAccountsByClientUuid(UUID clientUuid);

    @Transactional(readOnly = true)
    @LogDataSourceError
    List<Account> getAccountsByAccountUuid(List<UUID> accountUuids);

    @Transactional(readOnly = true)
    @LogDataSourceError
    List<Account> findAll();

    @Transactional
    @LogDataSourceError
    Account save(Account account);

    @Transactional
    @LogDataSourceError
    void delete(UUID accountUuid) throws AccountException;
}