package ru.t1.java.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.base.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByClientUuid(UUID clientUuid);

    @Override
    <S extends Client> List<S> saveAllAndFlush(Iterable<S> clients);
}