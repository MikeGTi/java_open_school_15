package ru.t1.java.service1.service.impl.after;


import org.springframework.stereotype.Component;
import ru.t1.java.service1.dto.TransactionResultDto;
import ru.t1.java.service1.model.enums.TransactionStatus;

import ru.t1.java.service1.repository.TransactionRepository;
import ru.t1.java.service1.service.Rule;

@Component
public class TransactionAcceptedRule extends Rule<TransactionResultDto> {

    private final TransactionRepository transactionRepository;

    public TransactionAcceptedRule(TransactionRepository transactionRepository) {
        super(transactionResultDto -> transactionResultDto.getTransactionStatus().equals(TransactionStatus.ACCEPTED));
        this.transactionRepository = transactionRepository;
    }

    @Override
    public boolean apply(TransactionResultDto transactionResultDto) {
        return super.apply(transactionResultDto);
    }

    @Override
    public boolean handle(TransactionResultDto transactionResultDto) {
        // update transaction status in DB
        transactionRepository.updateStatusByTransactionUuid(transactionResultDto.getTransactionUuid(), TransactionStatus.ACCEPTED);
        return true;
    }
}

