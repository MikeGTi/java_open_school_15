package ru.t1.java.locker.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.locker.dto.AccountDto;
import ru.t1.java.locker.exception.AccountException;
import ru.t1.java.locker.model.Account;
import ru.t1.java.locker.model.Transaction;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AccountMapper {

    private final TransactionMapper transactionMapper;

    public Account toEntity(AccountDto dto) throws AccountException {
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
