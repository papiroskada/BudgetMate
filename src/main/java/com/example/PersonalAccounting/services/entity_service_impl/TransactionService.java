package com.example.PersonalAccounting.services.entity_service_impl;

import com.example.PersonalAccounting.dao.TransactionDao;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.repositories.TransactionRepository;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService implements CrudService<Transaction> {

    private final TransactionRepository transactionRepository;
    private final TransactionDao transactionDao;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, TransactionDao transactionDao) {
        this.transactionRepository = transactionRepository;
        this.transactionDao = transactionDao;
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        User user = transaction.getUser();
        user.addTransaction(transaction);

        userFundsAddTransactions(transaction.getUser(), transaction);

        transaction.setDateTime(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void batchCreate(List<Transaction> transactions) {
        transactionDao.batchCreate(transactions);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAll(User user) {return transactionRepository.findByUser(user);}

    @Transactional(readOnly = true)
    public List<Transaction> getByPeriodic(boolean periodic) {
        return transactionRepository.findByPeriodic(periodic);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getByUserSinceDate(User user, LocalDate date) {
        return transactionRepository.findByUserAndDateTimeBetween(user, date.atStartOfDay(), LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public int getTransactionSumByUserAndRefill(User user, boolean refill) {
        return transactionRepository.getTransactionSumByUserAndRefill(user, refill);
    }

    @Transactional(readOnly = true)
    public Transaction getOne(int id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No transaction with such id: " + id));
    }

    @Transactional
    public Transaction update(int id, Transaction transaction) {
        transactionRepository.findById(id).ifPresentOrElse(t -> {
            User user = t.getUser();

            userFoundsCancelTransactions(user, t);
            userFundsAddTransactions(user, transaction);

            t.setSum(transaction.getSum());
            t.setDateTime(LocalDateTime.now());
            t.setCategory(transaction.getCategory());
            t.setComment(transaction.getComment());
            t.setRefill(transaction.isRefill());
        }, () -> {
            throw new NoSuchElementException("No transaction with id: " + id);
        });
        return transaction;
    }

    @Transactional
    public void delete(int id) {
        transactionRepository.findById(id).ifPresent(t -> {
            userFoundsCancelTransactions(t.getUser(), t);
            transactionRepository.delete(t);
        });
    }

    private void userFundsAddTransactions(User user, Transaction transaction) {
        int funds = user.getFunds();
        if(transaction.isRefill()) {
            user.setFunds(funds + transaction.getSum());
        }else {
            user.setFunds(funds - transaction.getSum());
        }
    }

    private void userFoundsCancelTransactions(User user, Transaction transaction) {
        int oldFunds = user.getFunds();
        if(transaction.isRefill()) {
            user.setFunds(oldFunds - transaction.getSum());
        }else {
            user.setFunds(oldFunds + transaction.getSum());
        }
    }
}
