package ru.t1.java.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.ClientMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class LegacyClientService {
    private final ClientRepository repository;
    private final Map<UUID, Client> cache;

    public LegacyClientService(ClientRepository repository) {
        this.repository = repository;
        this.cache = new HashMap<>();
    }

    @PostConstruct
    void init() {
        getClient(UUID.randomUUID());
    }

    public ClientDto getClient(UUID clientUuid) {
        log.debug("Call method getClient with id {}", clientUuid);
        ClientDto clientDto = null;

        if (cache.containsKey(clientUuid)) {
            return ClientMapper.toDto(cache.get(clientUuid));
        }

        try {
            Client entity = repository.findByClientUuid(clientUuid)
                    .orElseThrow(() -> new TransactionException(String.format("Client with uuid %s is not exists", clientUuid)));
            clientDto = ClientMapper.toDto(entity);
            cache.put(clientUuid, entity);
        } catch (Exception e) {
            log.error("Error: ", e);
//            throw new ClientException();
        }

//        log.debug("Client info: {}", clientDto.toString());
        return clientDto;
    }

}
