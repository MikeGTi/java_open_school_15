package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    @LogDataSourceError
    @Override
    public Account create(Account account) {
        //account.getTransactions().forEach(transaction -> transaction.setAccount(account));
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public Account findByAccountUuid(UUID accountUuid) {
        return accountRepository.findByAccountUuid(accountUuid)
                .orElseThrow(() -> new AccountException(String.format("Account with uuid %s is not exists", accountUuid)));
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public List<Account> findAccountsByAccountUuid(UUID accountUuid) throws AccountException {
        return accountRepository.findAllByAccountUuid(accountUuid)
                .orElseThrow(() -> new AccountException(String.format("Account with uuid %s is not exists", accountUuid)));
    }

    @Transactional
    @LogDataSourceError
    @Override
    public Account update(UUID accountUuid, Account accountDto) throws AccountException {
        Account account = accountRepository.findByAccountUuid(accountUuid)
                .orElseThrow(() -> new AccountException(String.format("Account with uuid %s is not exists", accountUuid)));

        account.setAccountUuid(accountDto.getAccountUuid());
        account.setStatus(accountDto.getStatus());
        account.setAccountType(accountDto.getAccountType());
        account.setBalance(accountDto.getBalance());
        account.setFrozenAmount(accountDto.getFrozenAmount());
        account.setClient(accountDto.getClient());
        account.setTransactions(accountDto.getTransactions());

        return accountRepository.save(account);
    }

    @Transactional
    @LogDataSourceError
    @Override
    public void delete(UUID accountUuid) throws AccountException {
        Account account = accountRepository.findByAccountUuid(accountUuid)
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
