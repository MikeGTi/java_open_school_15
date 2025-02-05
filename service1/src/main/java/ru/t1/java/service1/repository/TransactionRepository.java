package ru.t1.java.service1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.service1.model.Transaction;
import ru.t1.java.service1.model.enums.TransactionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByTransactionUuid(UUID transactionUuid);

    @Override
    <S extends Transaction> List<S> saveAllAndFlush(Iterable<S> transactions);

    @Modifying
    @Query("update Transaction t set t.status = ?2 where t.transactionUuid = ?1")
    void updateStatusByTransactionUuid(UUID transactionUuid, TransactionStatus transactionStatus);
}