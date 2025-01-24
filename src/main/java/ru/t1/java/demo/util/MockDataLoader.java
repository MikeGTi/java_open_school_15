package ru.t1.java.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.repository.TransactionRepository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Setter
@Getter
@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataLoader {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Value("${t1.mock-data.add-objects-counter}")
    private Integer clientsCounter;

    @Track
    @LogDataSourceError
    @PostConstruct
    void init() {
        int accountsCounter = 3;
        int transactionsCounter = 3;

        if (clientRepository.count() == 0) {
            log.info("Mock data creating, {} clients", clientsCounter);
            List<Client> clients = getClients(clientsCounter);
            List<Account> accounts = getAccounts(clients, accountsCounter);
            List<Transaction> transactions = getTransactions(accounts, transactionsCounter);

            clientRepository.saveAllAndFlush(clients);
            log.info("Mock data saved to DB, {} clients", clientsCounter);

            accountRepository.saveAllAndFlush(accounts);
            log.info("Mock data saved to DB, {} accounts", clientsCounter * accountsCounter);

            transactionRepository.saveAllAndFlush(transactions);
            log.info("Mock data saved to DB, {} transactions", clientsCounter * accountsCounter * transactionsCounter);
        }
    }

    private List<Client> getClients(int clientsCount) {
        return IntStream.range(0, clientsCount)
                        .mapToObj(i -> clientRepository.save(getFakeClient(i)))
                        .collect(Collectors.toCollection(() -> new ArrayList<>(clientsCount)));
    }

    private Client getFakeClient(int count) {
        return new Client("FirstNameTest" + count,
                "LastNameTest" + count,
                "MiddleNameTest" + count);
    }

    private List<Account> getAccounts(List<Client> clients, int accountsCount) {
        List<Account> accounts = new ArrayList<>(accountsCount);
        for (Client client : clients) {
            for (int t = 0; t < accountsCount; t++) {
                accounts.add(accountRepository.save(getFakeAccount(client)));
            }
        }
        return accounts;
    }

    private Account getFakeAccount(Client client) {
        Random random = new Random();

        AccountType accountType = AccountType.values()[random.nextInt(AccountType.values().length)];
        AccountStatus accountStatus = AccountStatus.values()[random.nextInt(AccountStatus.values().length)];
        int min =  1_000;
        int max = 150_000;

        return new Account(client,
                accountType,
                accountStatus,
                BigDecimal.valueOf(random.nextInt(min, max + 1)),
                new BigDecimal(0));
    }

    private List<Transaction> getTransactions(List<Account> accounts, int transactionsCount) {
        List<Transaction> transactions = new ArrayList<>(transactionsCount);
        for (Account account : accounts) {
            for (int t = 0; t < transactionsCount; t++) {
                transactions.add(transactionRepository.save(getFakeTransaction(account)));
            }
        }
        return transactions;
    }

    private Transaction getFakeTransaction(Account account) {
        Random random = new Random();
        TransactionStatus transactionStatus = TransactionStatus.values()[random.nextInt(TransactionStatus.values().length)];
        int min =  200;
        int max = 10_000;
        return new Transaction(account,
                               BigDecimal.valueOf(random.nextInt(min, max + 1)),
                               transactionStatus,
                               LocalDateTime.now());
    }

    @Track
    @LogDataSourceError
    public void loadData(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        InputStream inputStream = getClass().getResourceAsStream(filePath);

        List<Client> clients = objectMapper.readValue(inputStream, new TypeReference<>() {});
        clients.forEach(client -> client.getAccounts()
                .forEach(account -> {
                    account.setClient(client);
                    account.getTransactions()
                            .forEach(transaction -> transaction.setAccount(account));
                }));
        clientRepository.saveAll(clients);
    }
}
