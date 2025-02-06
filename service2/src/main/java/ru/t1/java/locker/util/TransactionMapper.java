package ru.t1.java.locker.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.locker.dto.TransactionAcceptedDto;
import ru.t1.java.locker.dto.TransactionDto;
import ru.t1.java.locker.dto.TransactionResultDto;
import ru.t1.java.locker.exception.TransactionException;
import ru.t1.java.locker.model.Account;
import ru.t1.java.locker.model.Transaction;
import ru.t1.java.locker.repository.AccountRepository;

import java.util.UUID;


@Component
@AllArgsConstructor
public class TransactionMapper {

    private final AccountRepository accountRepository;

    public Transaction toEntity(TransactionDto dto) throws TransactionException {
        UUID accountUuid = dto.getAccountUuid();
        Account account = accountRepository.findByAccountUuid(accountUuid)
           .orElseThrow(() -> new TransactionException(String.format("Account with uuid %s is not exists", accountUuid)));

        return Transaction.builder()
                .transactionUuid(dto.getTransactionUuid())
                .account(account)
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .created(dto.getCreated())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .transactionUuid(entity.getTransactionUuid())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .created(entity.getCreated())
                .accountUuid(entity.getAccount().getAccountUuid())
                .build();
    }

    public TransactionResultDto toResultDto(Transaction entity) {
        return TransactionResultDto.builder()
                .transactionUuid(entity.getTransactionUuid())
                .transactionStatus(entity.getStatus())
                .accountUuid(entity.getAccountUuid())
                .build();
    }

    public TransactionAcceptedDto toAcceptedDto(Transaction transaction, Account account, UUID clientUuid) {
        return TransactionAcceptedDto.builder()
                .clientUuid(clientUuid)
                .accountUuid(account.getAccountUuid())
                .transactionUuid(transaction.getTransactionUuid())
                .received(transaction.getCreated())
                //.received(LocalDateTime.now())
                .amount(transaction.getAmount())
                .balance(account.getBalance())
                .build();
    }
}
