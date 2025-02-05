package ru.t1.java.service1.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.service1.dto.AccountDto;
import ru.t1.java.service1.exception.AccountException;
import ru.t1.java.service1.model.Account;
import ru.t1.java.service1.model.Transaction;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AccountMapper {

    private final TransactionMapper transactionMapper;

    public Account toEntity(AccountDto dto) throws AccountException {
        /*UUID clientUuid = dto.getClientUuid();
        Client client = clientRepository.findByClientUuid(clientUuid)
                .orElseThrow(() -> new AccountException(String.format("Client with uuid %s is not exists", clientUuid)));*/

        Account account = Account.builder()
                .accountUuid(dto.getAccountUuid())

                .accountType(dto.getAccountType())
                .status(dto.getStatus())
                .balance(dto.getBalance())
                .frozenAmount(dto.getFrozenAmount())
                .build();

        Set<Transaction> transactionSet = new HashSet<>();
        if (dto.getTransactions() != null) {
            transactionSet = dto.getTransactions().stream()
                                                  .map(transactionMapper::toEntity)
                                                  .collect(Collectors.toSet());
        }
        account.setTransactions(transactionSet);
        transactionSet.forEach(transaction -> transaction.setAccount(account));
        return account;
    }

    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .accountUuid(entity.getAccountUuid())
                .clientUuid(entity.getClient().getClientUuid())
                .accountType(entity.getAccountType())
                .status(entity.getStatus())
                .balance(entity.getBalance())
                .frozenAmount(entity.getFrozenAmount())
                .transactions(entity.getTransactions().stream()
                                                      .map(transactionMapper::toDto)
                                                      .collect(Collectors.toSet()))
                .build();
    }
}
