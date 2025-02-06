package ru.t1.java.locker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.locker.dto.TransactionAcceptedDto;
import ru.t1.java.locker.service.Rule;
import ru.t1.java.locker.service.TransactionService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionAcceptedRuleApplierService implements TransactionService<TransactionAcceptedDto> {

    private final List<? extends Rule<TransactionAcceptedDto>> rules;

    public void applyAll(Iterable<TransactionAcceptedDto> transactions) {
        log.info("TransactionAcceptedRuleApplierService.applyAll(): starts");
        for (TransactionAcceptedDto transactionAcceptedDto : transactions) {
            int count = 0;
            for (Rule<TransactionAcceptedDto> rule : rules) {
                if (rule.apply(transactionAcceptedDto)) {
                    if (!rule.handle(transactionAcceptedDto)) {
                        log.warn("Transaction in TransactionAcceptedRuleApplierService.applyAll(), Not handled, uuid: {}", transactionAcceptedDto.getTransactionUuid());
                    }
                    break;
                } else if (rules.size() == count) {
                    log.warn("Transaction in TransactionAcceptedRuleApplierService.applyAll(), Not applied, uuid: {}", transactionAcceptedDto.getTransactionUuid());
                }
                count++;
            }
        }
        log.info("TransactionAcceptedRuleApplierService.applyAll(): ends");
    }
}
