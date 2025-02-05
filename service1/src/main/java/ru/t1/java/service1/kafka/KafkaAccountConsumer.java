package ru.t1.java.service1.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.service1.dto.AccountDto;
import ru.t1.java.service1.model.Account;
import ru.t1.java.service1.service.AccountService;
import ru.t1.java.service1.util.AccountMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer<T extends AccountDto> {

    private final AccountService accountService;
    private final AccountMapper accountMapper;


    @KafkaListener(id = "kafkaAccountListener",
                   topics = "${spring.kafka.topic.account-registration}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<T> messageList,
                         Acknowledgment ack) {
        log.debug("Account consumer: Обработка новых сообщений");

        try {
            List<Account> accounts = messageList.stream()
                                                .map(accountMapper::toEntity)
                                                .toList();
            accountService.saveAll(accounts);
        } finally {
            ack.acknowledge();
        }

        log.debug("Account consumer: записи обработаны");
    }
}
