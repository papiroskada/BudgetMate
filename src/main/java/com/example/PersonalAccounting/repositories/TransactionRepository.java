package com.example.PersonalAccounting.repositories;

import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t LEFT join FETCH t.user")
    List<Transaction> findAll();

    List<Transaction> findByUser(User user);

    List<Transaction> findByUserAndDateTimeBetween(User user, LocalDateTime start, LocalDateTime end);

    List<Transaction> findByPeriodic(boolean periodic);

    @Query("SELECT SUM(t.sum) FROM Transaction t WHERE t.user = ?1 AND t.refill = ?2")
    int getTransactionSumByUserAndRefill(User user, boolean refill);
}
