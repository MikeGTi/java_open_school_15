package ru.t1.java.service1.service.impl.after;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.service1.dto.TransactionResultDto;
import ru.t1.java.service1.service.Rule;
import ru.t1.java.service1.service.TransactionService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionRuleApplierAfterService implements TransactionService<TransactionResultDto> {

    private final List<? extends Rule<TransactionResultDto>> rules;

    public void applyAll(Iterable<TransactionResultDto> transactions) {
        log.info("TransactionRuleApplierAfterService.applyAll(): starts");
        for (TransactionResultDto transactionResultDto : transactions) {
            int count = 0;
            for (Rule<TransactionResultDto> rule : rules) {
                if (rule.apply(transactionResultDto)) {
                    if (!rule.handle(transactionResultDto)) {
                        log.warn("Transaction in TransactionAcceptedRuleApplierService.applyAll(), Not handled, uuid: {}", transactionResultDto.getTransactionUuid());
                    }
                    break;
                } else if (rules.size() == count) {
                    log.warn("Transaction in TransactionAcceptedRuleApplierService.applyAll(), Not applied, uuid: {}", transactionResultDto.getTransactionUuid());
                }
                count++;
            }
        }
        log.info("TransactionRuleApplierAfterService.applyAll(): ends");
    }
}
