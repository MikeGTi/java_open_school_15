package ru.t1.java.service1.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.t1.java.service1.model.Account;
import ru.t1.java.service1.repository.AccountRepository;
import ru.t1.java.service1.service.AccountService;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountSaverService implements AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public void saveAll(Iterable<Account> account) {
        accountRepository.saveAllAndFlush(account);
    }

}
