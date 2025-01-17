package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public Account findByUuid(UUID accountUuid) throws AccountException {
        Account account = accountRepository.findByAccountUuid(accountUuid)
                .orElseThrow(() -> new AccountException(String.format("Account with uuid %s is not exists", accountUuid)));
        return account;
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    public List<Account> findAccountsByClientUuid(UUID clientUuid) {
        Client client = clientRepository.findByClientUuid(clientUuid)
                .orElseThrow(() -> new AccountException(String.format("Client with uuid %s is not exists", clientUuid)));
        return accountRepository.findAllByClient(client);
    }

    @Transactional
    @LogDataSourceError
    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional
    @LogDataSourceError
    @Override
    public Account update(UUID uuid, Account account) {
        UUID clientUuid = account.getClient().getClientUuid();
        Client client = clientRepository.findByClientUuid(clientUuid)
                .orElseThrow(() -> new AccountException(String.format("Client with uuid %s, for Account with uuid %s is not exists", clientUuid, account.getAccountUuid())));
        Account updatedAccount = accountRepository.save(account);
        return updatedAccount;
    }

    @Transactional
    @LogDataSourceError
    @Override
    public Account create(Account account) {
        UUID clientUuid = account.getClient().getClientUuid();
        Client client = clientRepository.findByClientUuid(clientUuid)
                .orElseThrow(() -> new AccountException(String.format("Client with uuid %s, for Account with uuid %s is not exists", clientUuid, account.getAccountUuid())));
        Account savedAccount = accountRepository.save(account);
        return savedAccount;
    }

    @Transactional
    @LogDataSourceError
    @Override
    public void delete(UUID accountUuid) throws AccountException {
        Account account =  accountRepository.findByAccountUuid(accountUuid)
                .orElseThrow(() -> new AccountException(String.format("Account with uuid %s is not exists", accountUuid)));
        accountRepository.delete(account);
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    public List<Transaction> findAllTransactionsByAccountId(UUID accountUuid) throws AccountException {
        Account account = accountRepository.findByAccountUuid(accountUuid)
                .orElseThrow(() -> new AccountException(String.format("Account with uuid %s is not exists", accountUuid)));
        return transactionRepository.findAllTransactionsByAccount(account);
    }
}
