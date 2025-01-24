package ru.t1.java.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.base.model.Account;
import ru.t1.java.base.model.Transaction;
import ru.t1.java.base.model.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByTransactionUuid(UUID transactionUuid);

    List<Transaction> findAllTransactionsByAccount(Account account);

    List<Transaction> findAllTransactionsByCreatedBetween(LocalDateTime start, LocalDateTime end);

    @Override
    <S extends Transaction> List<S> saveAllAndFlush(Iterable<S> transactions);

    @Transactional
    @Modifying
    @Query("update Transaction t set t.status = ?2 where t.transactionUuid = ?1")
    void updateStatusByTransactionUuid(UUID transactionUuid, TransactionStatus transactionStatus);
}