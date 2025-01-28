package ru.t1.java.saver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.t1.java.saver.model.Account;
import ru.t1.java.saver.repository.AccountRepository;
import ru.t1.java.saver.service.AccountService;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public void save(Account account) {
        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void saveAll(Iterable<Account> account) {
        accountRepository.saveAllAndFlush(account);
    }

}
