package ru.t1.java.saver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.java.saver.model.Transaction;
import ru.t1.java.saver.model.enums.TransactionStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Override
    <S extends Transaction> List<S> saveAllAndFlush(Iterable<S> transactions);

    @Modifying
    @Query("update Transaction t set t.status = ?2 where t.transactionUuid = ?1")
    void updateStatusByTransactionUuid(UUID transactionUuid, TransactionStatus transactionStatus);
}