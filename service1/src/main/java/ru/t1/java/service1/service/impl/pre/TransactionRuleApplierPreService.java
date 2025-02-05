package ru.t1.java.service1.service.impl.pre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.service1.model.Transaction;
import ru.t1.java.service1.service.Rule;
import ru.t1.java.service1.service.TransactionService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionRuleApplierPreService implements TransactionService<Transaction> {

    private final List<? extends Rule<Transaction>> rules;

    public void applyAll(Iterable<Transaction> transactions) {
        log.info("TransactionRuleApplierPreService.applyAll(): starts");
        for (Transaction transaction : transactions) {
            int count = 0;
            for (Rule<Transaction> rule : rules) {
                if (rule.apply(transaction)) {
                    if (!rule.handle(transaction)) {
                        log.warn("Transaction in TransactionAcceptedRuleApplierService.applyAll(), Not handled, uuid: {}", transaction.getTransactionUuid());
                    }
                    break;
                } else if (rules.size() == count) {
                    log.warn("Transaction in TransactionAcceptedRuleApplierService.applyAll(), Not applied, uuid: {}", transaction.getTransactionUuid());
                }
                count++;
            }
        }
        log.info("TransactionRuleApplierPreService.applyAll(): ends");
    }
}
