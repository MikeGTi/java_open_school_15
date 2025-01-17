package ru.t1.java.demo.service;

import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    Client create(Client client);

    List<Client> findAll();

    Client findByClientUuid(UUID clientUuid) throws ClientException;

    List<Account> findAccountsByClientUuid(UUID clientUuid) throws ClientException;

    Client update(UUID clientUuid, Client clientDto) throws ClientException;

    void delete(UUID clientUuid) throws ClientException;
}
