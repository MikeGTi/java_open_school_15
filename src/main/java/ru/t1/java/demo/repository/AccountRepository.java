package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Override
    <S extends Account> List<S> saveAllAndFlush(Iterable<S> accounts);

    Optional<Account> findByAccountUuid(UUID accountUuid);

    Optional<List<Account>> findAllByClient(Client client);

    Optional<List<Account>> findAllByAccountUuid(UUID accountUuid);

    @Modifying
    @Query("update Account a set a.status = ?2 where a.accountUuid = ?1")
    void updateStatusByAccountUuid(UUID accountUuid, AccountStatus accountStatus);

    @Modifying
    @Query("update Account a set a.balance = ?2 where a.accountUuid = ?1")
    void updateBalanceByAccountUuid(UUID accountUuid, BigDecimal newBalance);

    @Modifying
    @Query("update Account a set a.frozenAmount = ?2 where a.accountUuid = ?1")
    void updateFrozenAmountByAccountUuid(UUID accountUuid, BigDecimal newFrozenAmount);
}