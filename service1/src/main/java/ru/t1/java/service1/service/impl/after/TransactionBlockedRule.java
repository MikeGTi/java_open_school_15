package ru.t1.java.service1.service.impl.after;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.t1.java.service1.dto.TransactionResultDto;
import ru.t1.java.service1.exception.TransactionException;
import ru.t1.java.service1.model.Account;
import ru.t1.java.service1.model.Transaction;
import ru.t1.java.service1.model.enums.AccountStatus;
import ru.t1.java.service1.model.enums.TransactionStatus;
import ru.t1.java.service1.repository.AccountRepository;
import ru.t1.java.service1.repository.TransactionRepository;
import ru.t1.java.service1.service.Rule;

@Slf4j
@Component
public class TransactionBlockedRule extends Rule<TransactionResultDto> {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionBlockedRule(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        super(transactionResultDto -> transactionResultDto.getTransactionStatus().equals(TransactionStatus.BLOCKED));
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean apply(TransactionResultDto transactionResultDto) {
        return super.apply(transactionResultDto);
    }

    @Override
    public boolean handle(TransactionResultDto transactionResultDto) {
        Transaction transaction;
        try {
             transaction = transactionRepository.findByTransactionUuid(transactionResultDto.getTransactionUuid())
                .orElseThrow(() -> new TransactionException(String.format("Transaction with uuid %s, not found", transactionResultDto.getTransactionUuid())));

        } catch (TransactionException e) {
            log.error(e.getMessage());
            return false;
        }

        Account account;
        try {
            account = accountRepository.findByAccountUuid(transactionResultDto.getAccountUuid())
                    .orElseThrow(() -> new TransactionException(String.format("Account uuid %s, for transaction with uuid %s, not found", transactionResultDto.getAccountUuid(),
                            transactionResultDto.getTransactionUuid())));
        } catch (TransactionException e) {
            log.error(e.getMessage());
            return false;
        }

        // update transaction status in DB
        transactionRepository.updateStatusByTransactionUuid(transaction.getTransactionUuid(), TransactionStatus.BLOCKED);

        // set account balance, frozenAmount, status
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        account.setFrozenAmount(account.getFrozenAmount().add(transaction.getAmount()));
        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);

        return true;
    }
}

