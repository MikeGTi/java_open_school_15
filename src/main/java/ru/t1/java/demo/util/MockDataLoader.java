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
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Track;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Setter
@Getter
@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataLoader {

    private final ClientRepository clientRepository;

    @Value("${t1.mock-data.add-objects-counter}")
    private Integer counter;

    @Value("${t1.mock-data.account-file-path}")
    private String accountFilePath;

    @Value("${t1.mock-data.client-file-path}")
    private String clientFilePath;

    @Value("${t1.mock-data.transaction-file-path}")
    private String transactionFilePath;

    @Value("${t1.mock-data.full-data-file-path}")
    private String fullDataFilePath;

    @PostConstruct
    void init() {
        if (clientRepository.count() == 0) {
            /*try {*/
                log.info("Loading data from {}", fullDataFilePath);
                /*loadData(fullDataFilePath);
                loadData(transactionFilePath);
                loadData(accountFilePath);
                loadData(clientFilePath);*/

                List<Client> clients = getFakeClients(100);
                clientRepository.saveAll(clients);

            /*} catch (IOException e) {
                log.error("Load data error", e);
            }*/
        }
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

    private List<Client> getFakeClients(int count) {
        int accountsCount = 3;
        int transactionsCount = 3;

        List<Client> clients = new ArrayList<>(count);
        List<Account> accounts = new ArrayList<>(accountsCount);
        List<Transaction> transactions = new ArrayList<>(transactionsCount);

        for (int i = 1; i < count + 1; i++) {
            transactions.clear();
            accounts.clear();

            Client client = getFakeClient(i);

            for (int a = 1; a < accountsCount + 1; a++) {
                Account account = getFakeAccount(client);
                accounts.add(account);

                for (int t = 1; t < transactionsCount + 1; t++) {
                    transactions.add(getFakeTransaction(client, account));
                }

                account.setTransactions(new HashSet<>(transactions));
            }

            client.setAccounts(accounts);
            clients.add(client);
        }

        return clients;
    }

    private Client getFakeClient(int count) {
        return new Client(UUID.randomUUID(),
                "FirstNameTest" + count,
                "LastNameTest" + count,
                "MiddleNameTest" + count);
    }

    private Account getFakeAccount(Client client) {
        Random random = new Random();

        AccountType accountType = AccountType.values()[random.nextInt(AccountType.values().length)];
        AccountStatus accountStatus = AccountStatus.values()[random.nextInt(AccountStatus.values().length)];

        return new Account(UUID.randomUUID(),
                            client,
                            accountType,
                            accountStatus,
                            BigDecimal.valueOf(random.nextDouble()),
                            new BigDecimal(0));
    }

    private Transaction getFakeTransaction(Client client, Account account) {
        Random random = new Random();
        TransactionStatus transactionStatus = TransactionStatus.values()[random.nextInt(TransactionStatus.values().length)];

        return new Transaction(UUID.randomUUID(),
                                account,
                                client,
                                BigDecimal.valueOf(random.nextDouble()),
                                transactionStatus,
                                LocalDateTime.now());
    }
}
