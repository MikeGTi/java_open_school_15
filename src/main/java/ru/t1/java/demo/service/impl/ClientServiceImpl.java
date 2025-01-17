package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.model.Client;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Transactional
    @LogDataSourceError
    @Override
    public Client create(Client client) {
        /*client.getAccounts().forEach(account -> {
            account.setClient(client);
            account.getTransactions().forEach(transaction -> transaction.setAccount(account));
        });*/
        return clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public Client findByClientUuid(UUID clientUuid) {
        return clientRepository.findByClientUuid(clientUuid)
                .orElseThrow(() -> new ClientException(String.format("Client with uuid %s is not exists", clientUuid)));
    }

    /*@Transactional(readOnly = true)
    @LogDataSourceError
    public Client findByAccountUuid(UUID accountUuid) {
        Account account = accountRepository.findByAccountUuid(accountUuid)
                .orElseThrow(() -> new ClientException(String.format("Client for account uuid %s is not exists", accountUuid)));
        return account.getClient();
    }*/

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public List<Account> findAccountsByClientUuid(UUID clientUuid) throws ClientException {
        Client client = clientRepository.findByClientUuid(clientUuid)
                .orElseThrow(() -> new ClientException(String.format("Client with uuid %s is not exists", clientUuid)));
        return accountRepository.findAllByClient(client)
                .orElseThrow(() -> new ClientException(String.format("Account for client uuid %s is not exists", clientUuid)));
    }

    @Transactional
    @LogDataSourceError
    @Override
    public Client update(UUID clientUuid, Client clientDto) throws ClientException {
        Client client = clientRepository.findByClientUuid(clientUuid)
                .orElseThrow(() -> new ClientException(String.format("Client with uuid %s is not exists", clientUuid)));

        client.setFirstName(clientDto.getFirstName());
        client.setMiddleName(clientDto.getMiddleName());
        client.setLastName(clientDto.getLastName());
        clientDto.getAccounts().forEach(client::addAccount);

        return clientRepository.save(client);
    }

    @Transactional
    @LogDataSourceError
    @Override
    public void delete(UUID clientUuid) throws ClientException {
        Client client = clientRepository.findByClientUuid(clientUuid)
                .orElseThrow(() -> new ClientException(String.format("Client with uuid %s is not exists", clientUuid)));
        clientRepository.delete(client);
    }

}
